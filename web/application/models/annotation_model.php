<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Annotation_model
*/
class Annotation_model extends CI_Model
{
	
	function __construct()
	{
		parent::__construct();
		$this->load->model('vocabulary_model');
	}

	function get_triples( $subject_entity_id, $obj_kind )
	{
		// simil sparql query! fetching all triples where subject == this feed item && object == keyword
		$object_tag_id = $this->vocabulary_model->get_tag_id( $obj_kind );
		$this->db->select('id');
		$this->db->where('subject_entity_id', $subject_entity_id);
		$this->db->where('object_tag_id', $object_tag_id);
		$query = $this->db->get('tagtriples');
		$response = array();
		foreach($query->result() as $row) {
			$response[] = $row->id;
		}
		return $response;
	}

	function annotate_feeditem_engine_keywords($feeditem_id, $engine, $keywords)
	{
		$vocabulary_id = $this->vocabulary_model->get_vocabulary_id( VOCABULARY_EXTRACTED, TRUE );
		$keyword_objects = $this->vocabulary_model->add_tags($vocabulary_id, $keywords);

		$struct_obj_feeditem_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_FEEDITEM );
		$struct_act_annotate_id = $this->vocabulary_model->get_tag_id( STRUCT_ACT_ANNOTATE );
		$struct_engine = $this->vocabulary_model->get_tag_id( $engine );
		$struct_obj_keyword_id = $this->vocabulary_model->get_tag_id( STRUCT_OBJ_KEYWORD );

		$tag_triples = array();
		foreach($keyword_objects as $keyword) {
			$data = array(
				'subject_tag_id' => $struct_obj_feeditem_id,
				'subject_entity_id' => $feeditem_id,
				'predicate_tag_id' => $struct_act_annotate_id,
				'predicate_entity_id' => $struct_engine,
				'object_tag_id' => $struct_obj_keyword_id,
				'object_entity_id' => $keyword->id
				);
			$this->db->insert('tagtriples', $data);
			$tag_triples[] = $this->db->insert_id();
		}
		return $tag_triples;
	}

	function annotate_micc_lda($feeditem_id, $keywords)
	{
		$tagtriples = $this->annotate_feeditem_engine_keywords( $feeditem_id, STRUCT_ENG_MICCLDA, $keywords );

		$this->db->where('id', $feeditem_id);
		$data = array(
			'sem_annotated' => 1
			);
		$this->db->update('feeditems', $data);
		return $tagtriples;
	}

	function annotate_teamlife_sanr($feeditem_id, $lang, $keywords)
	{
		$tagtriples = $this->annotate_feeditem_engine_keywords( $feeditem_id, STRUCT_ENG_TEAMLIFE, $keywords );
		$language_id = $this->vocabulary_model->get_language_id( $lang, TRUE );

		$this->db->where('id', $feeditem_id);
		$data = array(
			'language_id' => $language_id,
			'sem_annotated' => 1
			);
		$this->db->update('feeditems', $data);
		return $tag_triples;
	}
}
