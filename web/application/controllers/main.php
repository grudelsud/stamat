<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Main extends CI_Controller {

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
		if ( !$this->logged_in ) {
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

		$debug = '';
		$profile = null;
		if( $user ) {
			try {
				// Proceed knowing you have a logged in user who's authenticated.
				$profile = $this->facebook->api('/me?fields=id,name,link,email');
				$this->load->library('ion_auth');

				$login = $this->ion_auth->login($profile['email'], $profile['id']);

				if( !$login ) {
					$this->ion_auth->register($profile['name'], $profile['id'], $profile['email']);
					$this->logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
				}
			} catch (FacebookApiException $e) {
				// TODO: I'm sure we should do something here
			}
		}
		// echo $debug;
		redirect('/', 'refresh');
	}
}

/* End of main.php */
