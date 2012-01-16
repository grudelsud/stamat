<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Admin
*/
class Admin extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();
	}
	
	function index()
	{
		if (!$this->ion_auth->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		
		$data['template'] = 'feed';
		$this->load->view('admin_template', $data);
	}
}

/* end of admin.php */