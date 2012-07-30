<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Media_model
*/
class Media_model extends CI_Model
{
	function __construct()
	{
		parent::__construct();
	}

	// get media by id
	function get_media( $id )
	{
		$this->db->where('id', $id );
		$query = $this->db->get('feeditemmedia');
		return $query->result();
	}

	function get_media_array( $type, $primary, $flags, $min_width, $min_height, $page, $pagesize )
	{
		if( !empty($type) ) {
			$this->db->where('type', $type);
		}
		if( !empty($primary) ) {
			$this->db->where('primary', $primary);
		}
		if( !empty($flags) ) {
			$this->db->where('flags', $flags);
		}
		if( !empty($min_width) ) {
			$this->db->where('width >', $min_width);
		}
		if( !empty($min_height) ) {
			$this->db->where('height >', $min_height);
		}
		$limit = 100;
		$offset = 0;

		if( !empty($pagesize) && is_numeric($pagesize) && $pagesize > 0 ) {
			$limit = $pagesize;
		}
		if( !empty($page) && is_numeric($page) && $page > 0 ) {
			$offset = $limit * ($page - 1);
		}
		$this->db->limit($limit, $offset);
		$this->db->order_by('id', 'desc');

		$query = $this->db->get('feeditemmedia');
		return $query->result();
	}

	function update_flags( $id, $flags )
	{
		$data = array('flags' => $flags);
		$this->db->where('id', $id);
		$this->db->update('feeditemmedia', $data);
		return $this->db->affected_rows();
	}
}

/* end of media_model.php */