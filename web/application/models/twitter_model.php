<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

class Twitter_model extends CI_Model{
    
    function __construct()
	{
		parent::__construct();
	}
    
    function get_tweets( $key_word ){ // full text search by key word  
             $this->mongo_db->count('tweets'); //needed to clear the whereas array (bad library?? )
             $this->mongo_db->where(array('text' => $key_word));
             $query = $this->mongo_db->get('tokenize_text'); 
             
             $results = array();
             foreach($query as $extra_data_obj){  // fetch single tweet by _id       
                $this->mongo_db->count('tweets'); //needed to clear the whereas array (bad library?? )
                $query_obj = $this->mongo_db->where(array('_id' => $extra_data_obj['_id']))->get('tweets'); 
                $results[] = $query_obj[0];
              }
              return $results;
	} 
}


?>
