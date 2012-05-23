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
             $this->mongo_db->where(array('text' => $key_word));
             $query = $this->mongo_db->get('tokenize_text'); 
           
             $results = array();
             foreach($query as $text_obj){  // fetch single tweet by _id        
               $number = $this->mongo_db->count('tweets'); //needed to clear the whereas array O_O
               $query_obj = $this->mongo_db->where(array('_id' => $text_obj[_id]))->get('tweets');  
               $results[] = $query_obj[0];
              }
           
              return $results;
	} 
    
}



?>
