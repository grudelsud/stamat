<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Vocabulary_model
*/
class Vocabulary_model extends CI_Model
{
	function __construct()
	{
		parent::__construct();
	}


	/**
	 * Modifies a string to remove all non ASCII characters and spaces.
	 * Note : Works with UTF-8
	 * @param  string $string The text to slugify
	 * @return string         The slugified text
	 */
	static public function slugify( $string ) {
		$string = utf8_decode($string);
		$string = html_entity_decode($string);

		$a = 'ÀÁÂÃÄÅàáâãäåÒÓÔÕÖØòóôõöøÈÉÊËèéêëÇçÌÍÎÏìíîïÙÚÛÜùúûüÿÑñ';
		$b = 'AAAAAAaaaaaaOOOOOOooooooEEEEeeeeCcIIIIiiiiUUUUuuuuyNn';
		$string = strtr($string, utf8_decode($a), $b);

		$ponctu = array("?", ".", "!", ",");
		$string = str_replace($ponctu, "", $string);

		$string = trim($string);
		$string = preg_replace('/([^a-z0-9]+)/i', '-', $string);
		$string = strtolower($string);

		if (empty($string)) return 'n-a';

		return utf8_encode($string);
	}

	function add_tag( $vocabulary_id, $name, $parent_id = NULL )
	{
		$this->db->where('name', $name );
		$query = $this->db->get('tags');
		if( $query->num_rows() > 0 ) {
			$row = $query->row();
			$row->count += 1;
			$data = array('count' => $row->count );
			$this->db->where('id', $row->id);
			$this->db->update('tags', $data);
			return $row;
		} else {
			$data = array( 'vocabulary_id'=>$vocabulary_id, 'name'=>$name, 'slug' => $this->slugify($name) );
			if( !empty($parent_id) ) {
				$data['parent_id'] = $parent_id;
			}
			$query = $this->db->insert( 'tags', $data );
			$tag_id = $this->db->insert_id();
			return $this->get_tag( $tag_id );
		}
	}

	function get_vocabularies()
	{
		$query = $this->db->get('vocabularies');
		return $query->result();		
	}

	function get_tag( $tag_id )
	{
		$this->db->where('id', $tag_id);
		$query = $this->db->get('tags');
		return $query->result();
	}
	
	function get_tags( $vocabulary_id )
	{
		$this->db->where('vocabulary_id', $vocabulary_id);
		$query = $this->db->get('tags');
		return $query->result();
	}

	function delete_tags( $tag_ids )
	{
		foreach( $tag_ids as $tag_id ) {

			$this->db->where('id', $tag_id);
			$query = $this->db->get('tags');
			$parent_id = $query->row()->parent_id;
					
			$this->db->where('parent_id', $tag_id);
			$this->db->update('tags', array('parent_id'=>$parent_id));
			
			$this->db->delete('tags', array('id'=>$tag_id));
			$this->db->delete('feeds_tags', array('tag_id'=>$tag_id));
		}
		return true;
	}
}
