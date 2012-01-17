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
		$this->db->from('feeds as f');
		$this->db->join('feeds_tags as ft', 'f.id = ft.feed_id');
		$this->db->join('tags as t', 't.id = ft.tag_id');
		$this->db->where('f.id', $feed_id);
		$query = $this->db->get();
		return $query->result();
	}
	
	function get_feed( $id )
	{
		$this->db->where('id', $id );
		$query = $this->db->get('feeds');
		return $query->result();
	}
	
	function get_feeds( $user_id = NULL )
	{
		$this->db->where('user_id', $user_id );
		$query = $this->db->get('feeds');
		return $query->result();
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