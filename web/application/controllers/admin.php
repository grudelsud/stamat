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
		$this->data['template'] = 'feed';
	}
	
	function index()
	{
		if (!$this->ion_auth->logged_in())
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
}

/* end of admin.php */