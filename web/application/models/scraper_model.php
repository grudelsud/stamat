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
	
	private function _execute_curl( $rest_call, $request_type, $auth_type, $auth_params, $post_params ) {
		$this->curl->create( $rest_call );
		// $debug = $rest_call;
		if( $request_type == 'post' ) {
			$this->curl->post( $post_params );
			// $debug .= ' post params: '.var_export( $post_params, TRUE );
		}
		if( $auth_type == 'http_login' ) {
			$this->curl->http_login( $auth_params['username'], $auth_params['password'] );
			// $debug .= ' auth params: '.var_export( $auth_params, TRUE );
		}

		return $this->curl->execute();
	}

	private function _get_scraper( $name )
	{
		$this->db->where('name', $name);
		$result = $this->db->get('scrapers');
		return $result->row();	
	}

	function scrape_micc_lda( $feeditem_id )
	{
		// check if feeditem exists and is already annotated
		$this->db->where('id', $feeditem_id);
		$query = $this->db->get('feeditems');
		if( $query->num_rows() == 0 ) {
			return FALSE;
		} else {
			
			// check if feeditem is already annotated
			$row = $query->row();
			if( $row->sem_annotated ) {
				$this->load->model('annotation_model');
				return $this->annotation_model->get_triples( $feeditem_id, STRUCT_OBJ_KEYWORD );
			} else {
				
				$content = $this->get_scraped_content( $feeditem_id );
				$content = trim(preg_replace('/\s\s+/',' ',strip_tags($content)));
				// fetch from micc-lda
				$scraper = $this->_get_scraper('micc-lda');
				$auth_params = json_decode( $scraper->auth_params, TRUE );
				$post_params = json_decode( $scraper->post_params, TRUE );
				$post_params['text'] = $content;

				$response = $this->_execute_curl( $scraper->rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params );
				$response_obj = json_decode( $response );

				if( !empty($response_obj) ) {

					$this->load->model('annotation_model');
					$keywords = array();
					foreach ($response_obj->results as $keyword_obj) {
						$keywords[] = $keyword_obj->keyword;
					}
					$response = $this->annotation_model->annotate_micc_lda($feeditem_id, $keywords);
					return $response;			
				} else {
					return array('rest_call' => $scraper->rest_call, 'post_params' => $post_params, 'curl_info' => $this->curl->info, 'response' => $response);
				}
			}
		}
	}

	function scrape_teamlife_sanr( $feeditem_id )
	{
		// check if feeditem exists and is already annotated
		$this->db->where('id', $feeditem_id);
		$query = $this->db->get('feeditems');
		if( $query->num_rows() == 0 ) {
			return FALSE;
		} else {
			
			// check if feeditem is already annotated
			$row = $query->row();
			if( $row->sem_annotated ) {
				$this->load->model('annotation_model');
				return $this->annotation_model->get_triples( $feeditem_id, STRUCT_OBJ_KEYWORD );
			} else {
				
				$content = $this->get_scraped_content( $feeditem_id );
				$content = substr(trim(preg_replace('/\s\s+/',' ',strip_tags($content))), 0, 1999);
				// fetch from teamlife-sanr
				$scraper = $this->_get_scraper('teamlife-sanr');
				$rest_call = preg_replace('/{TEXT}/', urlencode($content), $scraper->rest_call);
				$post_params = json_decode( $scraper->post_params, TRUE );
				$auth_params = json_decode( $scraper->auth_params, TRUE );

				$response = $this->_execute_curl( $rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params );
				$response_obj = json_decode( $response );
				if( !empty($response_obj) ) {
					$keywords = explode(' ', $response_obj->keywords );

					// and store annotations
					$this->load->model('annotation_model');
					$response = $this->annotation_model->annotate_teamlife_sanr($feeditem_id, $response_obj->lang, $keywords);
					return $response;			
				} else {
					return array('content' => $content, 'rest_call' => $rest_call, 'auth_params' => $auth_params, 'curl_info' => $this->curl->info, 'response' => $response);
				}
			}
		}
	}

	function get_scraped_content( $feeditem_id )
	{
		// check if content is already scraped, or fetch it from readitlater
		$this->db->where('feeditem_id', $feeditem_id);
		$query = $this->db->get('feeditemcontents');
		if( $query->num_rows() > 0 ) {
			$row = $query->row();
			$content = $row->content;
		} else {
			$feeditemcontent_id = $this->scrape_readitlater( $feeditem_id );
			$this->db->where('id', $feeditemcontent_id);
			$query = $this->db->get('feeditemcontents');
			$row = $query->row();
			$content = $row->content;
		}
		return $content;
	}

	function scrape_readitlater( $feeditem_id )
	{
		$this->db->where('id', $feeditem_id);
		$query_item = $this->db->get('feeditems');
		$row_feeditem = $query_item->row();

		$scraper = $this->_get_scraper('readitlater');
		$rest_call = preg_replace('/{SCRAPE_URL}/', urlencode($row_feeditem->permalink), $scraper->rest_call);
		$post_params = json_decode( $scraper->post_params, TRUE );
		$auth_params = json_decode( $scraper->auth_params, TRUE );
		
		$response = $this->_execute_curl( $rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params );
		
		// create abstract and data insert object
		$abstract = substr(trim(preg_replace('/\s\s+/',' ',htmlspecialchars_decode(strip_tags($response)))), 0, 499);
		$data = array(
			'feeditem_id' => $feeditem_id,
			'scraper_id' => $scraper->id,
			'content' => trim($response),
			'abstract' => $abstract,
			'error_code' => $this->curl->error_code,
			'error_string' => $this->curl->error_string,
			'curl_info' => json_encode($this->curl->info)
		);
		
		// insert or update content
		$this->db->where('feeditem_id', $feeditem_id);
		$this->db->where('scraper_id', $scraper->id);
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
