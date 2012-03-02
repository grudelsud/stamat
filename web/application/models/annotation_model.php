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

	function get_triples( $subject_entity_id )
	{
		// simil sparql query! fetching all triples where subject == this feed item
		$this->db->select('id');
		$this->db->where('subject_entity_id', $subject_entity_id);
		$query = $this->db->get('tagtriples');
		$response = array();
		foreach($query->result() as $row) {
			$response[] = $row->id;
		}
		return $response;
	}

	function annotate_subject_engine_objects( $vocabulary, $subject_type, $subject_id, $engine, $object_type, $object_array )
	{
		$vocabulary_id = $this->vocabulary_model->get_vocabulary_id( $vocabulary, TRUE );
		$objects = $this->vocabulary_model->add_tags($vocabulary_id, $object_array);

		$subject_type_id = $this->vocabulary_model->get_tag_id( $subject_type );
		$struct_act_annotate_id = $this->vocabulary_model->get_tag_id( STRUCT_ACT_ANNOTATE );
		$struct_engine = $this->vocabulary_model->get_tag_id( $engine );
		$object_type_id = $this->vocabulary_model->get_tag_id( $object_type );

		$tag_triples = array();
		foreach($objects as $object) {
			$data = array(
				'subject_tag_id' => $subject_type_id,
				'subject_entity_id' => $subject_id,
				'predicate_tag_id' => $struct_act_annotate_id,
				'predicate_entity_id' => $struct_engine,
				'object_tag_id' => $object_type_id,
				'object_entity_id' => $object->id
			);
			$this->db->insert('tagtriples', $data);
			$tag_triples[] = $this->db->insert_id();
		}
		return $tag_triples;
	}

	function annotate_feeditem_engine_entities($feeditem_id, $engine, $entities)
	{
		return $this->annotate_subject_engine_objects( VOCABULARY_EXTRACTED_ENTITIES, STRUCT_OBJ_FEEDITEM, $feeditem_id, $engine, STRUCT_OBJ_ENTITY, $entities);
	}

	function annotate_feeditem_engine_topics($feeditem_id, $engine, $topics)
	{
		return $this->annotate_subject_engine_objects( VOCABULARY_EXTRACTED_TOPICS, STRUCT_OBJ_FEEDITEM, $feeditem_id, $engine, STRUCT_OBJ_TOPIC, $topics);
	}

	function annotate_feeditem_engine_keywords($feeditem_id, $engine, $keywords)
	{
		return $this->annotate_subject_engine_objects( VOCABULARY_EXTRACTED_TOPICS, STRUCT_OBJ_FEEDITEM, $feeditem_id, $engine, STRUCT_OBJ_KEYWORD, $keywords);
	}

	function annotate_micc_lda($feeditem_id, $topics, $entities)
	{
		$tagtriples_t = $this->annotate_feeditem_engine_topics( $feeditem_id, STRUCT_ENG_MICCLDA, $topics );
		$tagtriples_e = $this->annotate_feeditem_engine_entities( $feeditem_id, STRUCT_ENG_MICCLDA, $entities );

		$this->db->where('id', $feeditem_id);
		$data = array(
			'sem_annotated' => 1
			);
		$this->db->update('feeditems', $data);
		return array_merge($tagtriples_t, $tagtriples_e);
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
