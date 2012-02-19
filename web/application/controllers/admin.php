<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Admin
*/
class Admin extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();

		$this->load->model('user_model');
		// set default output template
		$this->data['template'] = 'feed';
	}
	
	function index()
	{
		if (!$this->user_model->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('admin_template', $this->data);
	}

	function feed()
	{
		$this->data['template'] = 'feed';
		$this->index();
	}

	function vocabulary()
	{
		$this->data['template'] = 'vocabulary';
		$this->index();
	}

	function items()
	{
		$this->data['template'] = 'items';
		$this->index();
	}

	function permalink( $feeditem_id = 0 )
	{
		$this->db->where('feeditem_id', $feeditem_id);
		$query = $this->db->get('feeditemcontents');
		if($query->num_rows() > 0) {
			$row = $query->row();
			$data['content'] = $row->content;
		} else {
			$data['content'] = '<p>empty record</p>';			
		}
		$this->load->view('permalink', $data);
	}
}

/* end of admin.php */