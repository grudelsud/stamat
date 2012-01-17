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
	
	function get_tags( $vocabulary_id )
	{
		$this->db->where('vocabulary_id', $vocabulary_id);
		$query = $this->db->get('tags');
		return $query->result();
	}
	
	function add_tag( $vocabulary_id, $name, $parent_id = NULL )
	{
		$this->db->where('name', $name );
		$query = $this->db->get('tags');
		if( $query->num_rows() > 0 ) {
			$row = $query->row();
			return $row->id;
		} else {
			$data = array( 'vocabulary_id'=>$vocabulary_id, 'name'=>$name );
			if( !empty($parent_id) ) {
				$data['parent_id'] = $parent_id;
			}
			$query = $this->db->insert( 'tags', $data );
			return $this->db->insert_id();
		}
	}
}
