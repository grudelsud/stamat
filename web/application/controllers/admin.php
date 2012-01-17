<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Admin
*/
class Admin extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();

		// set default output template
		$this->template = 'feed';
	}
	
	function index()
	{
		if (!$this->ion_auth->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		
		$data['template'] = $this->template;
		$this->load->view('admin_template', $data);
	}

	function feed()
	{
		$this->template = 'feed';
		$this->index();
	}

	function vocabulary()
	{
		$this->template = 'vocabulary';
		$this->index();
	}
}

/* end of admin.php */