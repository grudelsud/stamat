<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Vocabulary_model
*/
class Vocabulary_model extends CI_Model
{
	function __construct()
	{
		parent::__construct();
	}

	function add_tag( $vocabulary_id, $name, $parent_id = NULL )
	{
		$this->db->where('name', $name );
		$query = $this->db->get('tags');
		if( $query->num_rows() > 0 ) {
			$row = $query->row();
			return $row;
		} else {
			$data = array( 'vocabulary_id'=>$vocabulary_id, 'name'=>$name );
			if( !empty($parent_id) ) {
				$data['parent_id'] = $parent_id;
			}
			$query = $this->db->insert( 'tags', $data );
			$tag_id = $this->db->insert_id();
			return $this->get_tag( $tag_id );
		}
	}

	function get_vocabularies()
	{
		$query = $this->db->get('vocabularies');
		return $query->result();		
	}

	function get_tag( $tag_id )
	{
		$this->db->where('id', $tag_id);
		$query = $this->db->get('tags');
		return $query->result();
	}
	
	function get_tags( $vocabulary_id )
	{
		$this->db->where('vocabulary_id', $vocabulary_id);
		$query = $this->db->get('tags');
		return $query->result();
	}

	function delete_tags( $tag_ids )
	{
		foreach( $tag_ids as $tag_id ) {

			$this->db->where('id', $tag_id);
			$query = $this->db->get('tags');
			$parent_id = $query->row()->parent_id;
					
			$this->db->where('parent_id', $tag_id);
			$this->db->update('tags', array('parent_id'=>$parent_id));
			
			$this->db->delete('tags', array('id'=>$tag_id));
			$this->db->delete('feeds_tags', array('tag_id'=>$tag_id));
		}
		return true;
	}
}
