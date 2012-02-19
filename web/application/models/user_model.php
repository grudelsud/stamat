<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* User_model
*/
class User_model extends CI_Model
{
	var $user_id;
	
	function __construct()
	{
		parent::__construct();
		$this->user_id = $this->session->userdata('user_id');
	}
	
	function logged_in()
	{
		return $this->user_id;
	}
	
	function is_group( $name )
	{
		$this->db->from('users');
		$this->db->join('users_groups', 'users.id = users_groups.user_id');
		$this->db->join('groups', 'groups.id = users_groups.group_id');
		$this->db->where('groups.name', $name);
		$this->db->where('users.id', $this->user_id);
		
		$query = $this->db->get();
		return $query->num_rows();
	}

	function init_api_limit( $calls_left = HOURLYLIMIT_MEMBERS )
	{
		$data = array(
			'user_id' => $this->user_id,
			'calls_left' => $calls_left
		);
		$this->db->delete('apilimits', array('user_id' => $this->user_id));
		$this->db->insert('apilimits', $data);
	}

	function api_check()
	{
		if( !$this->logged_in() ) {
			$message = 'You must login first.';
			$this->output->set_status_header('401', $message);
			$this->output->set_output($message);
			exit();
		}
		if( !$this->is_group( GROUP_ADMIN ) ) {
			$this->db->where('user_id', $this->user_id);
			$query = $this->db->get('apilimits');
			if( $query->num_rows() > 0 ) {
				$row = $query->row();
				if( $row->calls_left > 0 ) {
					$data = array('calls_left' => $row->calls_left - 1);
					$this->db->where('user_id', $this->user_id);
					$this->db->update('apilimits', $data);
					return 200;
				} else {
					$time_elapsed = time() - strtotime( $row->created );
					$time_left = 3600 - $time_elapsed;
					if( $time_left < 0 ) {
						$this->init_api_limit();
					} else {
						$message = 'API limits reached, retry again in '.$time_left.' seconds.';
						$this->output->set_status_header('401', $message);
						$this->output->set_output($message);
						exit();
					}
				}
			} else {
				$this->init_api_limit();
			}
		}
	}

	function log( $name ) {
		$data = array(
			'user_id' => $this->user_id,
			'name' => $name
		);
		$this->db->insert('logs', $data);
	}
}

/* end of user_model.php */