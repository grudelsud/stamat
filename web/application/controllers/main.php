<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Main extends CI_Controller {

	function __construct()
	{
		parent::__construct();

		$this->load->model('user_model');

		// set default options & output template
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

	// not sure it's the right place, it's just that I don't want to add code to someone else's classes for maintenance (ion_auth in this case)
	function login_facebook()
	{
		$config = array(
			'appId' => FB_APP_ID,
			'secret' => FB_APP_SECRET,
			'fileUpload' => TRUE
		);
		$this->load->library('Facebook', $config);
		$user = $this->facebook->getUser();

		$profile = null;
		if( $user ) {
			try {
				// Proceed knowing you have a logged in user who's authenticated.
				$profile = $this->facebook->api('/me?fields=id,name,link,email');
				$this->load->library('ion_auth');

				$logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
				if( !$logged_in ) {
					$this->ion_auth->register($profile['name'], $profile['id'], $profile['email']);
					$logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
				}
			} catch (FacebookApiException $e) {
				// TODO: I'm sure we should do something here
			}
		}
		redirect('/', 'refresh');
	}
}

/* End of main.php */
