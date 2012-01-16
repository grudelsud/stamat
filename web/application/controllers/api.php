<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* API
*/
class Api extends CI_Controller
{
	function __construct()
	{
		parent::__construct();
	}
	
	function index()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
		} else {
			$this->_return_json_success('all good');
		}
	}
	
	function get_feeds()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		$this->load->model('feed_model');
		
		// TODO: should fetch logged user.id here, but bloody ion_auth->user() doesn't seem to work
		$this->_return_json_success( $this->feed_model->get_feeds( 1 ) );
	}
	
	function add_feed()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		
		$title = $this->input->post('title');
		$url = $this->input->post('url');
		
		if( $title && $url ) {
			$this->load->model('feed_model');
			
			// TODO: should use dynamic user.id here, but ion_auth->user() doesn't seem to work
			$this->_return_json_success( $this->feed_model->add_feed( $title, $url, 1 ) );
		} else {
			$this->_return_json_error('empty fields');
		}
	}

	// returns success message in json
	private function _return_json_success($success) {
		$this->_return_json('success', $success);
	}
	
	// returns error message in json
	private function _return_json_error($error) {
		$this->_return_json('error', $error);
	}
	
	// returns a json array
	private function _return_json($response, $message) {
		$data = array(
			'json' => array(
				$response => $message
			)
		);
		$this->load->view('json', $data);
	}
}

/* end of api.php */