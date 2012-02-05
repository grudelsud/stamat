<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Scraper_model
*/
class Scraper_model extends CI_Model
{
	
	function __construct()
	{
		parent::__construct();
	}
	
	function scrape_readitlater($feeditem_id)
	{
		$this->db->where('id', $feeditem_id);
		$query_item = $this->db->get('feeditems');
		$row = $query_item->row();

		$source = 'read it later';
		$url = 'http://text.readitlaterlist.com/v2/text?apikey='.READITLATER_API_KEY.'&images=1&url='.urlencode($row->permalink);
		$content = $this->curl->simple_get($url);
		$data = array(
			'feeditem_id' => $feeditem_id,
			'scraper_id' => 1,
			'content' => $content
		);

		// $this->curl->error_code; // int
		// $this->curl->error_string;

		// Information
		// $this->curl->info; // array

		// TODO: replace static scraper data with things read from db
		$this->db->where('feeditem_id', $feeditem_id);
		$this->db->where('scraper_id', 1);
		$query_content = $this->db->get('feeditemcontents');

		if( $query_content->num_rows() == 0 ) {
			$this->db->insert('feeditemcontents', $data);
			return $this->db->insert_id();
		} else {
			$row = $query_content->row();
			$this->db->where('id', $row->id);
			$this->db->update('feeditemcontents', $data);
			return $row->id;
		}
	}
}
