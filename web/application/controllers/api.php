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
	
	/**
	 * Feed Items CRUD (no update)
	 */
	function fetch_store_all_feeds()
	{
		$result = array();
		$this->load->model('feed_model');
		$feeds = $this->feed_model->get_feeds();
		foreach( $feeds as $feed ) {
			$this->load->library('rss_parser');
			$this->rss_parser->set_feed_url( $feed->url );
			$feed_content = $this->rss_parser->get_feed();

			foreach ($feed_content->get_items() as $item) {
				$item_md5id = md5( $item->get_id() );
				$this->db->where('item_md5id', $item_md5id);
				$query = $this->db->get('feeditems');
				if( $query->num_rows() == 0 ) {
					$data = array(
						'feed_id' => $feed->id,
						'item_md5id' => $item_md5id,
						'title' => $item->get_title(),
						'permalink' => $item->get_permalink(),
						'date' => $item->get_date('Y-m-d H:i:s'),
						'description' => $item->get_description(),
						'abstract' => substr(strip_tags($item->get_description()), 0, 499)
					);
					$this->db->insert('feeditems', $data);
					$result[] = $item_md5id;
				}
			}
		}
		$this->_return_json_success( $result );			
	}
	
	// fetch all content scraping pages referenced by permalinks
	function fetch_store_all_permalinks()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		if( $feed_id = $this->input->post('feed_id') ) {
			$result = array();
			$this->load->model('scraper_model');

			$this->db->where('feed_id', $feed_id);
			$query = $this->db->get('feeditems');
			foreach($query->result() as $row) {
				$result[] = $this->scraper_model->scrape_readitlater($row->id);
			}
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feed_id');
		}		
	}

	function fetch_store_permalink()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		if( $feeditem_id = $this->input->post('feeditem_id') ) {
			$result = array();
			$this->load->model('scraper_model');
			$result[] = $this->scraper_model->scrape_readitlater($feeditem_id);
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feeditem_id');
		}		
	}

	function load_feed_items()
	{
		if (!$this->ion_auth->logged_in()) {
			$this->_return_json_error('please login first');
			return;
		}

		if( $feed_id = $this->input->post('feed_id') ) {			

			$this->db->where('feed_id', $feed_id);
			$this->db->order_by('date', 'desc');
			$query = $this->db->get('feeditems');

			$result = array();
			foreach ($query->result() as $row) {
				$result_item = new stdClass();

				$result_item->id = $row->id;
				$result_item->title = $row->title;
				$result_item->permalink = $row->permalink;
				$result_item->description = $row->description;
				$result_item->date = $row->date;
								
				$result[] = $result_item;
			}
			
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feed_id');
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