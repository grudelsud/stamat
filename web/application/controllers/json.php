<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Json
*/
class Json extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();
		$this->load->model('user_model');

		$logged_user = array();
		$this->logged_in = $this->user_model->logged_in( $logged_user );
		$this->logged_user = $logged_user;
	}

	public function index()
	{
		$this->_return_json_success('');
	}

	public function tags()
	{
		$this->db->from('tags as t');
		$this->db->join('vocabularies as v', 't.vocabulary_id = v.id');

		if( $this->logged_in ) {
			$this->db->where('v.user_id', $this->logged_user['id'] );
		} else {
			$this->db->where('v.user_id', 1);
		}
		$this->db->where('v.name', VOCABULARY_SYS_TAGS);
		$query = $this->db->get();
		return $this->_return_json_success( $query->result() );
	}

	public function feeds()
	{
		$params = $this->uri->uri_to_assoc();
		// return $this->_return_json_success( $params );

		$this->db->from('feeds as f');
		if( $this->logged_in ) {
			$this->db->where('user_id', $this->logged_user['id'] );
		}
		if( !empty($params['tag']) ) {
			$this->db->join('feeds_tags as ft', 'f.id = ft.feed_id');
			$this->db->join('tags as t', 't.id = ft.tag_id');
			$this->db->where('t.slug', $params['tag']);
		}
		$query = $this->db->get();
		return $this->_return_json_success( $query->result() );
	}

	public function items()
	{

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