<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* User_model
*/
class User_model extends CI_Model
{
	function __construct()
	{
		parent::__construct();
	}
	
	function log( $name ) {
		$user_id = $this->session->userdata('user_id');
		$data = array(
			'user_id' => $user_id,
			'name' => $name
		);
		$this->db->insert('logs', $data);
	}
}

/* end of user_model.php */