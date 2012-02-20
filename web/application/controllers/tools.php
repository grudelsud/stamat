<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* tools
*/
class Tools extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();
		$this->data['output'] = '';
		$this->load->model('user_model');
	}

	function index()
	{
		if (!$this->user_model->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('tools', $this->data);
	}
	
	function create_slugs()
	{
		$output = '<h1>tag slugs created</h1>';
		$query = $this->db->get('tags');
		
		$this->load->model('vocabulary_model');
		foreach( $query->result() as $row ) {
			$slug = $this->vocabulary_model->slugify( $row->name );
			$output .= $slug.'<br/>';
			$data = array('slug' => $slug);
			$this->db->where('id', $row->id);
			$this->db->update('tags', $data);
		}
		$this->data['output'] = $output;
		$this->index();
	}
}
