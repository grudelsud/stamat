<?php
class Scraper_tools_model extends CI_Model {
    /*
        this model contains methods that are in common with twitter, facebook, and other social networks scrapers
    */
    var $image_file_system_path = 'D:/data/images/';   // destination directory of images; (probably this isn't the right 
                                                       // place for a costant) 
    public function __construct(){
		
        parent::__construct();
    }
    
    function get_image_file_system_path (){  
        return $this->image_file_system_path;
    }
    
    // download a remote image by curl
    function download_remote_file_with_curl($file_url, $save_to){
    	$ch = curl_init();
	curl_setopt($ch, CURLOPT_POST, 0); 
	curl_setopt($ch,CURLOPT_URL,$file_url); 
	curl_setopt($ch, CURLOPT_RETURNTRANSFER, 1); 
	$file_content = curl_exec($ch);
	curl_close($ch);
 
        $downloaded_file = fopen($save_to, 'w');
	fwrite($downloaded_file, $file_content);
	fclose($downloaded_file);
    }
    
    // create the "db name" of an image
    function get_image_name_md5 ($url, $source, $source_id){
        $string = $url.$source.$source_id;   // source is: 'twitter' or 'facebook' or...
        $image_name = md5($string);
        return $image_name;
    }
    // delete all urls from text, before tokenize 
    function delete_urls_from_text($text, $urls){
        foreach($urls as $single_url){  
            $text = str_replace($single_url, "", $text);
        }
        return $text;
    }    

    // tokenize text     
    function tokenizer ($text){
        // eliminate upper case
        $lover_case_text = strtolower($text); 
        // eliminate the punctuation: "stripped string" 
        $stripped_string = preg_replace('/[^a-z0-9]+/i', ' ', $lover_case_text);       
        $tok = strtok($stripped_string, " \n\t");
        $text_array = array();
        while ($tok !== false) {
              $text_array[] = $tok;
              $tok = strtok(" \n\t");
        }
        return $text_array;
    }
    
    // check the presence of a remote image
    function image_detector ($url){
     
        $params = array('http' => array('method' => 'HEAD'));
        $ctx = stream_context_create($params);
        $fp = @fopen($url, 'rb', false, $ctx);
        if (!$fp) 
            return false;  // Problem with url

        $meta = stream_get_meta_data($fp);
         if ($meta === false){
                fclose($fp);
                return false;  // Problem reading data from url
        }
        $wrapper_data = $meta["wrapper_data"];
        if(is_array($wrapper_data)){
            foreach(array_keys($wrapper_data) as $hh){
                if (substr($wrapper_data[$hh], 0, 19) == "Content-Type: image"){ // strlen("Content-Type: image") == 19 
                   
                    fclose($fp);
                    return true;
                }
             }
         }
        fclose($fp);
        return false;
    }
   
// check if a word is in the text
    function word_is_in($text, $word){
         // eliminate upper case
        $lover_case_text = strtolower($text); 
        // eliminate the punctuation: "stripped string" 
        $stripped_string = preg_replace('/[^a-z0-9]+/i', ' ', $lover_case_text);    
        return strpos($stripped_string, $word);
    }
 
// make hash of an image file
    function get_image_md5($image_url){
        $byte_string = file_get_contents($image_url);  // reads entire file into a string
        $byte_string_hash = md5($byte_string);
        return $byte_string_hash;
    }
    
    
}
?>
