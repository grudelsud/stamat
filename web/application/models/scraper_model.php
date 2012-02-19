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
	
	function scrape_teamlife_sanr($feeditem_id)
	{
		$this->db->where('id', $feeditem_id);
		$query_item = $this->db->get('feeditems');
		$row = $query_item->row();

		$this->curl->create('http://beta.teamlife.it/sanr/ajax/extract_words.php?snippets=on&text='.urlencode($row->abstract));
		$this->curl->http_login('guest', 'teamlife');
		$response = $this->curl->execute();

		return json_decode($response);
	}

	function scrape_readitlater($feeditem_id)
	{
		$this->db->where('id', $feeditem_id);
		$query_item = $this->db->get('feeditems');
		$row = $query_item->row();

		$source = 'read it later';
		$url = 'http://text.readitlaterlist.com/v2/text?apikey='.READITLATER_API_KEY.'&images=1&url='.urlencode($row->permalink);
		$content = $this->curl->simple_get($url);
		$abstract = substr(trim(preg_replace('/\s\s+/',' ',htmlspecialchars_decode(strip_tags($content)))), 0, 499);
		$data = array(
			'feeditem_id' => $feeditem_id,
			'scraper_id' => 1,
			'content' => trim( $content ),
			'abstract' => $abstract,
			'error_code' => $this->curl->error_code,
			'error_string' => $this->curl->error_string,
			'curl_info' => json_encode($this->curl->info)
		);

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
