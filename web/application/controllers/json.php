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

	/**
	 * accepts param tag in the uri so it can be called as below:
	 *
	 * base_url/json/feeds - this will return all the feeds of the user currently logged in
	 * base_url/json/feeds/tag/fashion - this will return the list of feeds tagged fashion for the user currently logged in
	 */
	public function feeds()
	{
		$params = $this->uri->uri_to_assoc();

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

		/**
		 * ok, now that we have all the data we need, we should organize the output this way:
		 * [{id:X, title:Y, url:Z, tags:[{id:x, name:y, slug:z}]}, ...]
		 */
		$query = $this->db->get();
		$result = array();
		foreach ($query->result() as $row) {
			if(empty($result[$row->id])) {
				$feed = new stdClass();
				$feed->id = $row->id;
				$feed->title = $row->title;
				$feed->url = $row->url;
				$feed->tags = array();
				$result[$row->id] = $feed;
			}
			$tag = new stdClass();
			$tag->id = $row->tag_id;
			$tag->name = $row->tag_name;
			$tag->slug = $row->slug;
			$result[$row->id]->tags[] = $tag;
		}
		return $this->_return_json_success( array_values($result) );
	}

	public function feeditems()
	{
		$params = $this->uri->uri_to_assoc();

		$this->db->select('fi.id, fi.feed_id, fi.title, fi.permalink, fi.date, fi.description, f.title as feed_title, f.url');
		$this->db->from('feeditems as fi');
		$this->db->join('feeds as f', 'fi.feed_id = f.id');

		$meta = new stdClass();
		$meta->params = '';
		if(!empty($params['tag'])) {
			$this->db->join('feeds_tags as ft', 'f.id = ft.feed_id');
			$this->db->join('tags as t', 't.id = ft.tag_id');
			$this->db->where('t.slug', $params['tag']);
			$meta->params = 'tag/'.$params['tag'].'/';
		}
		if(!empty($params['id'])) {
			$this->db->where('f.id', $params['id']);
			$meta->params = 'id/'.$params['id'].'/';
		}
		if( $this->logged_in ) {
			$this->db->where('f.user_id', $this->logged_user['id'] );
		}
		$this->db->order_by('date', 'desc');

		$meta->page = 1;
		$meta->pagesize = 20;
		if(!empty($params['page'])) {
			if(!is_numeric($params['page'])) {
				$meta->page = 1;
			} else {
				$meta->page = (int)($params['page'] > 0 ? $params['page'] : 1);
			}
		}
		$this->db->limit($meta->pagesize, $meta->pagesize * ($meta->page - 1));
		$query = $this->db->get();

		$items = array();
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

			$this->db->where('feeditem_id', $row->id);
			$this->db->where('type', 'image');
			$this->db->order_by('primary', 'desc');
			$query_media = $this->db->get('feeditemmedia');
			if($query_media->num_rows() > 0) {
				$item->pic = $query_media->row()->url;
			}

			$items[] = $item;
		}
		$result = new stdClass();
		$result->items = $items;

		// now rebuild the query to count the results for pagination
		$this->db->from('feeditems as fi');
		$this->db->join('feeds as f', 'fi.feed_id = f.id');

		if(!empty($params['tag'])) {
			$this->db->join('feeds_tags as ft', 'f.id = ft.feed_id');
			$this->db->join('tags as t', 't.id = ft.tag_id');
			$this->db->where('t.slug', $params['tag']);
		}
		if(!empty($params['id'])) {
			$this->db->where('f.id', $params['id']);
		}
		if( $this->logged_in ) {
			$this->db->where('f.user_id', $this->logged_user['id'] );
		}
		$meta->count_all_results = $this->db->count_all_results();
		$meta->count_all_pages = ceil($meta->count_all_results / $meta->pagesize);

		$meta->prev = $meta->params.'page/'.($meta->page > 1 ? $meta->page - 1 : 1);
		$meta->next = $meta->params.'page/'.($meta->page < $meta->count_all_pages ? $meta->page + 1 : $meta->count_all_pages);
		$result->meta = $meta;

		return $this->_return_json_success( $result );
	}

	/**
	 * so we need to pull out something like the following:
	 * {success: {
	 *  	tags: [{id:x, name:y, slug:z, type:a}, ...], 
	 *  	media: [], 
	 *  	content:[]
	 * }}
	 */
	public function reactions()
	{
		$params = $this->uri->uri_to_assoc();
		$content = new stdClass();
		$tags = array();
		$media = array();

		if(!empty($params['id'])) {
			$this->db->select('fi.id, fi.title, fi.permalink, fi.date, fic.abstract, fic.content');
			$this->db->from('feeditems as fi');
			$this->db->join('feeditemcontents as fic', 'fi.id = fic.feeditem_id', 'left');
			$this->db->where('fi.id', $params['id']);
			$query = $this->db->get();
			if( $query->num_rows() > 0 ) {
				$row = $query->row();
				$content->id = $row->id;
				$content->title = $row->title;
				$content->permalink = $row->permalink;
				$content->date = $row->date;
				$content->abstract = $row->abstract;
				$content->content = $row->content;
			}

			$this->db->select('t.id, t.name, t.slug, v.name as type');
			$this->db->from('tags as t');
			$this->db->join('tagtriples as tt', 't.id = tt.object_entity_id');
			$this->db->join('vocabularies as v', 't.vocabulary_id = v.id');
			$this->db->where('t.stop_word', 0);
			$this->db->where('tt.subject_entity_id', $params['id']);

			$query = $this->db->get();
			foreach ($query->result() as $row) {
				$tags[] = $row;
			}

			$this->db->where('feeditem_id', $params['id']);
			$this->db->order_by('primary desc, type asc');
			$query = $this->db->get('feeditemmedia');
			foreach ($query->result() as $row) {
				$media[] = $row;
			}
		}

		$result = new stdClass();
		$result->content = $content;
		$result->tags = $tags;
		$result->media = $media;
		return $this->_return_json_success( $result );
	}

	/**
	 * Media related, pure REST!
	 */
	function media()
	{
		$this->load->model('media_model');
		$params = $this->uri->uri_to_assoc();

		if(empty($params['action'])) {
			$params['action'] = 'browse';
		}

		switch( $params['action'] ) {
			case 'browse':
				$type = empty($params['type']) ? null : $params['type'];
				$primary = empty($params['primary']) ? null : $params['primary'];
				if(!empty($params['flags']) && $params['flags'] == 'invalid') {
					$flags = MEDIA_INVALID;
				} else {
					$flags = MEDIA_DOWNLOADED & MEDIA_QUEUEDFORINDEXING & MEDIA_INDEXED;					
				}
				// $min_width = empty($params['min_width']) ? null : $params['min_width'];
				$min_width = 300;
				$min_height = empty($params['min_height']) ? null : $params['min_height'];
				$page = empty($params['page']) ? null : $params['page'];
				// $pagesize = empty($params['pagesize']) ? null : $params['pagesize'];
				$pagesize = 30;

				$media = $this->media_model->get_media_array($type, $primary, $flags, $min_width, $min_height, $page, $pagesize);
				foreach ($media as $row) {
					$row->url_src = $row->url;
					$row->url = $row->abs_path . $row->hash;
				}
				$output = new stdClass;
				$output->media = $media;
				$this->_return_json_success($output);
				break;
			case 'read':
				$media = $this->media_model->get_media($params['read']);
				foreach ($media as $row) {
					$row->url_src = $row->url;
					$row->url = $row->abs_path . $row->hash;
				}
				$output = new stdClass;
				$output->media = $media;
				$this->_return_json_success($output);
				break;
			default:
				$this->_return_json_error('select /action/* amongst: browse, read');
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