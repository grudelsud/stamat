<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Main extends CI_Controller {

	function __construct()
	{
		parent::__construct();

		$this->load->model('user_model');
		// set default output template
		$this->data['template'] = 'home';
	}
	
	function index()
	{
		if (!$this->user_model->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('main_template', $this->data);
	}
}

/* End of main.php */
