<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class H4e extends CI_Controller {

	function __construct()
	{
		parent::__construct();

		$this->load->model('user_model');

		$this->load->config('ion_auth', TRUE);
		$admin_group = $this->config->item('admin_group', 'ion_auth');

		$logged_user = array();
		$this->logged_in = $this->user_model->logged_in( $logged_user );

		// set default options & output template
		$this->data['logged_user'] = $logged_user;
		$this->data['logged_admin'] = empty($logged_user['groups']) ? false : in_array( $admin_group, $logged_user['groups']);
		$this->data['template'] = 'home';
	}
	
	function index()
	{
		$this->load->view('h4e_template', $this->data);
	}
}

/* end of hack4europe */