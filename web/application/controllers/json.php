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

		$this->db->select('f.id, f.title, f.url, t.id as tag_id, t.name as tag_name, t.slug');
		$this->db->from('feeds as f');

		$this->db->join('feeds_tags as ft', 'f.id = ft.feed_id');
		$this->db->join('tags as t', 't.id = ft.tag_id');

		if( $this->logged_in ) {
			$this->db->where('f.user_id', $this->logged_user['id'] );
		}
		if( !empty($params['tag']) ) {
			$this->db->where('t.slug', $params['tag']);
		}

		$query = $this->db->get();
		return $this->_return_json_success( $query->result() );
	}

	public function feeditems()
	{
		$params = $this->uri->uri_to_assoc();

		$this->db->select('fi.id, fi.feed_id, fi.title, fi.permalink, fi.date, fi.description, f.title as feed_title, f.url');
		$this->db->from('feeditems as fi');
		$this->db->join('feeds as f', 'fi.feed_id = f.id');

		if( $this->logged_in ) {
			$this->db->where('f.user_id', $this->logged_user['id'] );
		}
		$this->db->order_by('date', 'desc');
		$this->db->limit(20);
		$query = $this->db->get();

		$result = array();
		foreach($query->result() as $row) {
			$item = new stdClass();
			$item->id = $row->id;
			$item->feed_id = $row->feed_id;
			$item->title = strip_tags($row->title);
			$item->permalink = $row->permalink;
			$item->date = $row->date;
			$item->description = strip_tags( $row->description, '<div><p><a>');
			$item->feed_title = $row->feed_title;
			$item->url = $row->url;

			$result[] = $item;
		}
		return $this->_return_json_success( $result );
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