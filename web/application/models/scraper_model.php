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
	
	private function _execute_curl( $rest_call, $request_type, $auth_type, $auth_params, $post_params, &$debug = NULL )
	{
		$this->curl->create( $rest_call );
		$debug['rest_call'] = $rest_call;

		$this->curl->option(CURLOPT_TIMEOUT, 60);

		if( $auth_type == 'http_login' ) {
			$this->curl->http_login( $auth_params['username'], $auth_params['password'] );
			$debug['auth'] = $auth_params;
		}
		if( $request_type == 'post' ) {
			$this->curl->post( $post_params );
			$debug['post'] = $post_params;
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
			$annotations = (int)$row->sem_annotated;
			if( $annotations & ANNOTATED_MICC ) {
				$this->load->model('annotation_model');
				return $this->annotation_model->get_triples( $feeditem_id );
			} else {
				
				$content = $this->get_scraped_content( $feeditem_id );
				$content = trim(preg_replace('/\s\s+/',' ',html_entity_decode(strip_tags($content), ENT_NOQUOTES )));
				// fetch from micc-lda
				$scraper = $this->_get_scraper('micc-lda');
				$auth_params = json_decode( $scraper->auth_params, TRUE );
				$post_params = json_decode( $scraper->post_params, TRUE );
				$post_params['text'] = $content;
                                
                                
				$response_t = $this->_execute_curl( $scraper->rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params );
				$response_obj = json_decode( $response_t );
				$topics = array();
				foreach ($response_obj->results as $keyword_obj) {
					$topics[] = $keyword_obj->keyword;
				}

				$post_params['analysis'] = 'ned';
				$response_e = $this->_execute_curl( $scraper->rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params );
				$response_obj = json_decode( $response_e );
				$entities = array();
				foreach ($response_obj->results as $keyword_obj) {
					$entities[] = $keyword_obj->keyword;
				}

				$this->load->model('annotation_model');
				$response = $this->annotation_model->annotate_micc_lda($feeditem_id, $topics, $entities, $annotations);
				return $response;			
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
			$annotations = (int)$row->sem_annotated;
			if( $annotations & ANNOTATED_SANR ) {
				$this->load->model('annotation_model');
				return $this->annotation_model->get_triples( $feeditem_id );
			} else {
				
				$content = $this->get_scraped_content( $feeditem_id );
				$content = substr(urlencode(trim(preg_replace('/\s\s+/',' ',strip_tags($content)))), 0, 1999);
				// fetch from teamlife-sanr
				$scraper = $this->_get_scraper('teamlife-sanr');
				$rest_call = preg_replace('/{TEXT}/', $content, $scraper->rest_call);
				$post_params = json_decode( $scraper->post_params, TRUE );
				$auth_params = json_decode( $scraper->auth_params, TRUE );

				$debug = array();
				$response = $this->_execute_curl( $rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params, $debug );
				$response_obj = json_decode( $response );
				if( !empty($response_obj) ) {
					$keywords = explode(' ', $response_obj->keywords );

					// and store annotations
					$this->load->model('annotation_model');
					$response = $this->annotation_model->annotate_teamlife_sanr($feeditem_id, $response_obj->lang, $keywords, $annotations);
					return $response;			
				} else {
					return array('debug' => $debug, 'curl_info' => $this->curl->info, 'response' => $response);
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
        
        function scrape_twitter( $key_word ) 
	{
		$scraper = $this->_get_scraper('twitter_scraper');
		$rest_call = preg_replace('/{KEY_WORD}/',  urlencode($key_word) , $scraper->rest_call);          
                                        
                $post_params = json_decode( $scraper->post_params, TRUE ); 
		$auth_params = json_decode( $scraper->auth_params, TRUE );           
		$response = $this->_execute_curl( $rest_call, $scraper->request_type, $scraper->auth_type, $auth_params, $post_params, $debug );
                $response_obj = json_decode( $response );
                  
		foreach ($response_obj->results as $tweet_obj) { 
                       // store all "urls" from tweet: "url" and "expanded url"
                       $tweet_exp_urls = array(); 
                       $tweet_urls = array();
                       foreach($tweet_obj->entities->urls as $urls_obj){
                           $tweet_exp_urls[] = $urls_obj->expanded_url;
                           $tweet_urls[] = $urls_obj->url; // used after, for removing urls before tokenize
                       }
                       $images_urls = array(); // a tweet can contain more than one image
                       //find the image from the "media" array 
                       $this->load->model('scraper_tools_model'); 
                       foreach($tweet_obj->entities->media as $media_obj) {  
                            if($media_obj->type == 'photo') { $images_urls[] = $media_obj->media_url;}
                       }
                       // check for remote images from tweet "expanded urls"
                       foreach($tweet_exp_urls as $url){
                          $is_an_image  = $this->scraper_tools_model->image_detector($url);
                          if($is_an_image == TRUE){ $images_urls[] = $url;}  
                       }
                       // store all "user mentions" from tweet
                       $tweet_user_mentions = array();                                        
                       foreach($tweet_obj->entities->user_mentions as $user_mentions_obj){
                           $user_m = array (
                            'id' => $user_mentions_obj->id,
                           'id_str'=> $user_mentions_obj->id_str,
                           'screen_name'=> $user_mentions_obj->screen_name,
                           'name'=> $user_mentions_obj->name,
                           'indices' => $user_mentions_obj->indices
                           );
                           $tweet_user_mentions[] = $user_m;  
                       }                       
                       $data = array(
                           // using the tweet ID (string version) instead the mongoId object
                           '_id' => $tweet_obj->id_str,  
                           'ids' => array(
                               'tweet_id' => $tweet_obj->id,
                               'tweet_id_str' => $tweet_obj->id_str,
                               'from_user_id' => $tweet_obj->from_user_id,
                               'to_user_id' => $tweet_obj->to_user_id,
                               ),
                           'from_user_name' => $tweet_obj->from_user_name,
                           'from_user' => $tweet_obj->from_user,
                           'to_user_name' => $tweet_obj->to_user_name,
                           'to_user' => $tweet_obj->to_user,
                           'source' => $tweet_obj->source,
                           'metadata' => array(
                             'result_type' => $tweet_obj->metadata->result_type,
                             'recent_retweets' => $tweet_obj->metadata->recent_retweets
                           ),
                           'iso_language_code' => $tweet_obj->iso_language_code,
                           'profile_image_url' => $tweet_obj->profile_image_url,
                           'expanded_urls' => $tweet_exp_urls,
                           'urls' => $tweet_urls,
                           'user_mentions' => $tweet_user_mentions,
                           'text' => $tweet_obj->text,
                           'images_urls' => $images_urls,
                           'created_at' => $tweet_obj->created_at,
                       );            
                       // check if the tweet already exists in db (no duplicates)
                       $this->mongo_db->where(array ('_id' => $tweet_obj->id_str)); 
                       $query = $this->mongo_db->get('tweets');
                       if($query == NULL){
                             $this->mongo_db->insert('tweets', $data);
                             // tokenized text for full text search
                             $string = $this->scraper_tools_model->delete_urls_from_text($data[text],$tweet_urls);
                             $text_array = $this->scraper_tools_model->tokenizer($string);
                             // download images on file system
                             $images_names = array();  // the name of every images file, from the current tweet
                             if($data[images_urls] != NULL){
                                 foreach ($data[images_urls] as $im_url){                                                                                                                 
                                        $directory_path = $this->scraper_tools_model->get_image_file_system_path();
                                        $image_name = $this->scraper_tools_model->get_image_md5($im_url,'twitter',$data[_id]);// hash of the image
                                        $file_system_path = $directory_path.$image_name; // completely path of the image
                                        $this->scraper_tools_model->download_remote_file_with_curl($im_url, $file_system_path ); 
                                        $images_names[] = $image_name;
                                }
                             }
                             // insert the extra data for the tweet
                             $extra_data = array('_id' => $tweet_obj->id_str, 
                                                 'text' => $text_array, //tokenized text       
                                                 'images_names' => $images_names,  // the hash string
                                                 'error_code' => $this->curl->error_code,
                                                 'error_string' => $this->curl->error_string,
                                                 'curl_info' => json_encode($this->curl->info)
                                            );
                             $this->mongo_db->insert('tokenize_text', $extra_data);
                       }
                       
                }                  
                return;
        }        
}
