<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Annotation_model
*/
class Annotation_model extends CI_Model
{
	
	function __construct()
	{
		parent::__construct();
	}
	
	function annotate_teamlife_sanr($feeditem_id, $lang, $keywords)
	{
		$this->load->model('vocabulary_model');
		$language_id = $this->vocabulary_model->get_language_id( $lang, TRUE );
		$vocabulary_id = $this->vocabulary_model->get_vocabulary_id( VOCABULARY_EXTRACTED, TRUE );

		$keyword_objects = $this->vocabulary_model->add_tags($vocabulary_id, $keywords);
		
		$struct_obj_feeditem_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_FEEDITEM );
		$struct_act_annotate_id = $this->vocabulary_model->get_tag_id( STRUCT_ACT_ANNOTATE );
		$struct_eng_teamlife_id = $this->vocabulary_model->get_tag_id( STRUCT_ENG_TEAMLIFE );
		$struct_obj_keyword_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_KEYWORD );

		$tag_triples = array();
		foreach($keyword_objects as $keyword) {
			$data = array(
				'subject_tag_id' => $struct_obj_feeditem_id,
				'subject_entity_id' => $feeditem_id,
				'predicate_tag_id' => $struct_act_annotate_id,
				'predicate_entity_id' => $struct_eng_teamlife_id,
				'object_tag_id' => $struct_obj_keyword_id,
				'object_entity_id' => $keyword->id
			);
			$this->db->insert('tagtriples', $data);
			$tag_triples[] = $this->db->insert_id();
		}

		$this->db->where('id', $feeditem_id);
		$data = array(
			'language_id' => $language_id,
			'sem_annotated' => 1
		);
		// $this->db->update('feeditems', $data);
		return $tag_triples;
	}
}
