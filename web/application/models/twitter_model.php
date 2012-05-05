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
    
    function get_tweets( $key_word)  // get all tweets by key word
	{
           //get the query id for the next step: search tweets by query id 	   
           $this->db->where('key_word', $key_word );
	   $query_content = $this->db->get('twitter_queries');        
           $row = $query_content->row();
           $query_id = (int)$row->id;
           // if i try to display absent tweet ???
           if ($query_id == NULL) {
               return NULL; // vedere meglio cosa fare...
           }
           // get tweets for this query id
           $this->db->where('query_id', $query_id);     
           $tweets = $this->db->get('tweets');
           return $tweets->result();
	}   
        
}



?>
