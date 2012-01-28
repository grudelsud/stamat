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
	
	function load_feed_items()
	{
		// if (!$this->ion_auth->logged_in()) {
		// 	$this->_return_json_error('please login first');
		// 	return;
		// }
		// 
		// $url = $this->input->post('url');

		$url = 'http://www.repubblica.it/rss/homepage/rss2.0.xml';
		if( $url ) {
			$this->load->library('rss_parser');

			$this->rss_parser->set_feed_url( $url );
			$feed = $this->rss_parser->get_feed();
			
			$result = array();
			foreach ($feed->get_items() as $item) {
				$result_item = new stdClass();

				$result_item->title = $item->get_title();
				$result_item->permalink = $item->get_permalink();
				$result_item->description = $item->get_description();
				$result_item->date = $item->get_date('j F Y - g:i a');
								
				$result[] = $result_item;
			}
			
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty url');
		}
	}
	
	/**
	 * Feed related CRUD functions (no update)
	 * 
	 * add_feed - add feed to database
	 * get_feed - read feed + tag details by id
	 * get_feeds - get full feed list
	 * delete_feed - delete feed by id
	 */
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
			$id = $this->feed_model->add_feed( $title, $url, 1 );
			$this->_return_json_success( $this->feed_model->get_feed( $id ) );
		} else {
			$this->_return_json_error('empty fields');
		}
	}
	
	function get_feed()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		if( $feed_id = $this->input->post('feed_id') ) {
			$this->load->model('feed_model');

			$result = new stdClass();
			$result->feed = $this->feed_model->get_feed($feed_id);
			$result->tags = $this->feed_model->get_tags($feed_id);
			$this->_return_json_success( $result );
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
		$this->_return_json_success( $this->feed_model->get_feeds( TRUE ) );
	}
	
	function delete_feed()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		$feed_id = $this->input->post('feed_id');
		if( $feed_id ) {
			$this->load->model('feed_model');
			$this->_return_json_success($this->feed_model->delete_feed( $feed_id ));
		} else {
			$this->_return_json_error('empty field id');
		}
	}

	/**
	 * Feed/tag related CRUD functions (no update)
	 * 
	 * add_feed_tag - add feed / tag association to database
	 * get_feed_tags - read  tags associated to feed by feed_id
	 * delete_feed_tags - delete feed association by comma separated tag_id
	 */
	function add_feed_tag()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		$feed_id = $this->input->post('feed_id');
		$tag_ids = $this->input->post('tag_id');
		
		if( $feed_id && $tag_ids ) {
			$this->load->model('feed_model');
			$result = $this->feed_model->add_tags( $feed_id, explode(',', $tag_ids) );
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('both feed and tags must be selected');
		}
	}

	function get_feed_tags()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		if( $feed_id = $this->input->post('feed_id') ) {
			$this->load->model('feed_model');
			$this->_return_json_success( $this->feed_model->get_tags($feed_id) );
		}
	}
	
	function delete_feed_tags()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		$tag_ids = $this->input->post('tag_id');
		
		if( $tag_ids ) {
			$this->load->model('feed_model');
			$result = $this->feed_model->delete_tags( explode(',', $tag_ids) );
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('select tags 1st');
		}		
	}

	/**
	 * Vocabulary/tag related CRUD functions (no update)
	 * 
	 * add_tag - add tag to database
	 * get_vocabulary_tags - read tags associated to vocabulary by vocabulary_id
	 * delete_tags - delete tags by comma separated tag_id
	 */
	function add_tag()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		
		$tag = $this->input->post('tag');
		$parent = $this->input->post('parent_id');
		
		if( $tag ) {
			$this->load->model('vocabulary_model');
			// TODO: set variable vocabulary_id
			$id = $this->vocabulary_model->add_tag( 1, $tag, empty( $parent ) ? NULL : $parent );
			// TODO: return something clever
			$this->_return_json_success( $id );
		}
		
	}

	function get_vocabulary_tags()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		$this->load->model('vocabulary_model');
		// TODO: set variable vocabulary_id
		$this->_return_json_success( $this->vocabulary_model->get_tags( 1 ) );
	}

	function delete_tags()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}
		$tag_ids = $this->input->post('tag_id');
		
		if( $tag_ids ) {
			$this->load->model('vocabulary_model');
			$result = $this->vocabulary_model->delete_tags( explode(',', $tag_ids) );
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('select tags 1st');
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