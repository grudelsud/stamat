<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* API
*/
class Api extends CI_Controller
{
	function __construct()
	{
		parent::__construct();
		$this->load->model('user_model');
	}
	
	function index()
	{
		$this->_user_check('API - index');
		$this->_return_json_success('all good');
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
		$this->_user_check('API - fetch_store_all_permalinks');
	
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
		$this->_user_check('API - fetch_store_permalink');
	
		if( $feeditem_id = $this->input->post('feeditem_id') ) {
			$result = array();
			$this->load->model('scraper_model');
			$result[] = $this->scraper_model->scrape_readitlater($feeditem_id);
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feeditem_id');
		}		
	}
	
	function fetch_entities()
	{
		$this->_user_check('API - fetch_entities');
	
		if( $feeditem_id = $this->input->post('feeditem_id') ) {
			$result = array();
			$this->load->model('scraper_model');
			if( $this->input->post('annotate_micc') == 1 ) {
				$result['micc'] = $this->scraper_model->scrape_micc_lda($feeditem_id);
			} else if( $this->input->post('annotate_teamlife') == 1 ) {
				$result['sanr'] = $this->scraper_model->scrape_teamlife_sanr($feeditem_id);
			} else {
				$this->load->model('annotation_model');
				$result = $this->annotation_model->get_triples($feeditem_id);
			}
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feeditemcontents_id');
		}		
	}
	
	function count_feed_items()
	{
		$this->_user_check();
		// pagination used for admin.items.js
		if( $feed_id = $this->input->post('feed_id') ) {
			$this->db->where('feed_id', $feed_id);
			$this->db->from('feeditems');
			$this->_return_json_success( $this->db->count_all_results() );
		// pagination used for admin.topics.js
		} else if( $tag_array = $this->input->post('tag_array') ) {
			$this->load->model('vocabulary_model');
			$subject_type_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_FEEDITEM );

			$this->db->where('subject_tag_id', $subject_type_id);
			$first = TRUE;
			foreach( $tag_array as $tag_id ) {
				if( $first ) {
					$this->db->where('object_entity_id', $tag_id);
					$first = FALSE;
				} else {
					$this->db->or_where('object_entity_id', $tag_id);					
				}
			}
			$this->db->from('tagtriples');
			$this->_return_json_success( $this->db->count_all_results() );
		} else {
			$this->_return_json_error('empty feed_id');
		}
	}
	
	function load_feed_items()
	{
		$this->_user_check();
	
		if( $feed_id = $this->input->post('feed_id') ) {			
	
			$this->db->where('feed_id', $feed_id);
			$this->db->order_by('date', 'desc');
			
			$offset = $this->input->post('offset') ? $this->input->post('offset') : 0;
			$limit = $this->input->post('limit') ? $this->input->post('limit') : 100;
			$query = $this->db->get('feeditems', $limit, $offset);
	
			$result = array();
			foreach ($query->result() as $row) {
				$result_item = new stdClass();
	
				$result_item->id = $row->id;
				$result_item->title = $row->title;
				$result_item->permalink = $row->permalink;
				$result_item->description = $row->description;
				$result_item->date = $row->date;
	
				$content_id = array();
				$this->db->select('id');
				$this->db->where('feeditem_id', $row->id);
				$query_content = $this->db->get('feeditemcontents');
				foreach( $query_content->result() as $row_content ) {
					$content_id[] = $row_content->id;
				}
				$result_item->content_id = $content_id;
	
				$result[] = $result_item;
			}
			
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('empty feed_id');
		}
	}
	
	function load_tagged_feed_items()
	{
		$this->_user_check();

		if( $tag_array = $this->input->post('tag_array') ) {

			$this->load->model('vocabulary_model');
			$subject_type_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_FEEDITEM );

			$this->db->select('feeditems.id, feeditems.title, feeditems.permalink, feeditems.description, feeditems.date');
			$this->db->distinct();
			$this->db->from('feeditems');
			$this->db->join('tagtriples', 'feeditems.id = tagtriples.subject_entity_id');
			$this->db->group_by('feeditems.id');
			$this->db->where('tagtriples.subject_tag_id', $subject_type_id);
			$first = TRUE;
			foreach( $tag_array as $tag_id ) {
				if( $first ) {
					$this->db->where('tagtriples.object_entity_id', $tag_id);
					$first = FALSE;
				} else {
					$this->db->or_where('tagtriples.object_entity_id', $tag_id);					
				}
			}

			$this->db->order_by('date', 'desc');
			$offset = $this->input->post('offset') ? $this->input->post('offset') : 0;
			$limit = $this->input->post('limit') ? $this->input->post('limit') : 100;
			$this->db->limit($limit, $offset);
			$query = $this->db->get();
			
			$this->_return_json_success( $query->result() );
		} else {
			$this->_return_json_error('empty triples');
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
		$this->_user_check('API - add_feed');
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
		$this->_user_check();
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
		$this->_user_check();
		$this->load->model('feed_model');
		
		// TODO: should fetch logged user.id here, but bloody ion_auth->user() doesn't seem to work
		$this->_return_json_success( $this->feed_model->get_feeds( TRUE ) );
	}
	
	function delete_feed()
	{
		$this->_user_check('API - delete_feed');
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
		$this->_user_check('API - add_feed_tag');
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
		$this->_user_check();
	
		if( $feed_id = $this->input->post('feed_id') ) {
			$this->load->model('feed_model');
			$this->_return_json_success( $this->feed_model->get_tags($feed_id) );
		}
	}
	
	function delete_feed_tags()
	{
		$this->_user_check('API - delete_feed_tags');
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
		$this->_user_check('API - add_tag');
		
		if( $tag = $this->input->post('tag') ) {
			$parent = $this->input->post('parent_id');
			$this->load->model('vocabulary_model');
			$vocabulary_id = $this->input->post('vocabulary_id') ? $this->input->post('vocabulary_id') : 1;
			$result[] = $this->vocabulary_model->add_tag( $vocabulary_id, $tag, empty( $parent ) ? NULL : $parent );
			$this->_return_json_success( $result );
		}
		
	}
	
	function get_vocabularies()
	{
		$this->_user_check();
		$this->load->model('vocabulary_model');
		$this->_return_json_success( $this->vocabulary_model->get_vocabularies() );
	}
	
	function get_vocabulary_tags()
	{
		$this->_user_check();
		$this->load->model('vocabulary_model');
		$vocabulary_id = $this->input->post('vocabulary_id') ? $this->input->post('vocabulary_id') : 1;
		$this->_return_json_success( $this->vocabulary_model->get_tags( $vocabulary_id ) );
	}
	
	function delete_tags()
	{
		$this->_user_check('API - delete_tags');
		$tag_ids = $this->input->post('tag_id');
		
		if( $tag_ids ) {
			$this->load->model('vocabulary_model');
			$result = $this->vocabulary_model->delete_tags( explode(',', $tag_ids) );
			$this->_return_json_success( $result );
		} else {
			$this->_return_json_error('select tags 1st');
		}
	}

	private function _user_check($message = '')
	{
		if( $this->user_model->api_check() ) {
			if( !empty($message) ) {
				$this->user_model->log( $message );
			}
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