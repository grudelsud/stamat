<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Twitter_model extends CI_Model{
    
    function __construct()
	{
		parent::__construct();
	}
    // full text search by key word  
    function get_tweets($key_word){ 
             $this->mongo_db->count('tweets'); //needed to clear the whereas array (bad library?? )
             $this->mongo_db->where(array('text' => $key_word));
             $query = $this->mongo_db->get('tokenize_text'); 
             
             $results = array();
             foreach($query as $extra_data_obj){  // fetch single tweet by _id       
                $this->mongo_db->count('tweets'); //needed to clear the whereas array (bad library?? )
                $query_obj = $this->mongo_db->where(array('_id' => $extra_data_obj['_id']))->get('tweets'); 
                // return only the used fields of tweets
                $result_obj = new stdClass();
		$result_obj->_id = $query_obj[0]['_id'];
		$result_obj->text = $query_obj[0]['text'];
		$result_obj->created_at = $query_obj[0]['created_at'];
                $result_obj->images_urls = $query_obj[0]['images_urls'];
                $results[] = $result_obj;
              }
              return $results;
	} 
   // check the presence in the db of an image (by its byte hash)     
   function check_image_presence($hash_byte){
        $this->mongo_db->count('tokenize_text'); //needed to clear the whereas array (bad library?? )
        $this->mongo_db->where(array('image_hash' => $hash_byte));
        $query = $this->mongo_db->get('tokenize_text');
        if($query != null){return true;}
        else return false;
   }
}
?>
