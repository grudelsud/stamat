<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Feed_model
*/
class Feed_model extends CI_Model
{
	function __construct()
	{
		parent::__construct();
	}
	
	function get_tags( $feed_id )
	{
		$this->db->select('ft.id, ft.tag_id, t.name');
		$this->db->from('feeds_tags as ft');
		$this->db->join('feeds as f', 'f.id = ft.feed_id');
		$this->db->join('tags as t', 't.id = ft.tag_id');
		$this->db->where('f.id', $feed_id);
		$query = $this->db->get();
		return $query->result();
	}
	
	function delete_tags( $tag_ids )
	{
		foreach( $tag_ids as $tag_id ) {
			$this->db->delete('feeds_tags', array('id'=>$tag_id));
		}
		return true;
	}
	
	function add_tags( $feed_id, $tag_ids )
	{
		$result = array();
		foreach( $tag_ids as $tag_id ) {
			$this->db->where('id', $tag_id);
			$query = $this->db->get('tags');
			
			// check if tag exists first
			if( $query->num_rows() > 0 ) {
				$row = $query->row();
				$data = new stdClass();
				$data->tag_id = $row->id;
				$data->name = $row->name;

				$insert_data = array( 'feed_id'=>$feed_id, 'tag_id'=>$tag_id );
				$query = $this->db->insert( 'feeds_tags', $insert_data );
				$data->id = $this->db->insert_id();
				$result[] = $data;
			}
		}
		return $result;
	}

	function get_feed( $id )
	{
		$this->db->where('id', $id );
		$query = $this->db->get('feeds');
		return $query->result();
	}
	
	function get_feeds( $add_tags = FALSE, $user_id = NULL )
	{
		if( $user_id ) {
			$this->db->where('user_id', $user_id );			
		}
		$query = $this->db->get('feeds');
		if( $add_tags ) {
			$result = array();
			foreach( $query->result() as $row ) {
				$tags = $this->get_tags( $row->id );
				$tag_array = array();
				foreach( $tags as $tag ) {
					$tag_array[] = $tag->name;
				}
				$row->tags = implode( ', ', $tag_array );
				$result[] = $row;
			}
			return $result;
		} else {
			return $query->result();			
		}
	}
	
	function delete_feed( $feed_id )
	{
		$this->db->delete('feeds', array('id'=>$feed_id));
		return true;
	}

	function add_feed( $title, $url, $user_id )
	{
		$this->db->where('title', $title );
		$this->db->or_where('url', $url );
		$query = $this->db->get('feeds');
		if( $query->num_rows() > 0 ) {
			$row = $query->row();
			return $row->id;
		} else {
			$data = array( 'title'=>$title, 'url'=>$url, 'user_id'=>$user_id );
			$query = $this->db->insert( 'feeds', $data );
			return $this->db->insert_id();
		}
	}
}

/* end of feed_model.php */