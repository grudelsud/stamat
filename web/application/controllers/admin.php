<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/**
* Admin
*/
class Admin extends CI_Controller
{
	
	function __construct()
	{
		parent::__construct();

		$this->load->model('user_model');
		$this->load->config('ion_auth', TRUE);
		$admin_group = $this->config->item('admin_group', 'ion_auth');

		$logged_user = array();
		$this->logged_in = $this->user_model->logged_in( $logged_user );

		// set default options & output template
		$this->data['logged_user'] = $logged_user;
		$this->data['logged_admin'] = empty($logged_user['groups']) ? false : in_array( $admin_group, $logged_user['groups']);
		// set default output template
		$this->data['template'] = 'feed';
	}
	
	function index()
	{
		if (!$this->user_model->logged_in())
		{
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('admin_template', $this->data);
	}

	function logodetection()
	{
		$this->data['template'] = 'logodetection';
		$this->index();
	}

	function feed()
	{
		$this->data['template'] = 'feed';
		$this->index();
	}

	function vocabulary()
	{
		$this->data['template'] = 'vocabulary';
		$this->index();
	}

	function items()
	{
		$this->data['template'] = 'items';
		$this->index();
	}

	function topics()
	{
		$this->data['template'] = 'topics';
		$this->index();
	}

	function users()
	{
		$this->load->library('grocery_CRUD');

		$this->grocery_crud->set_table('users');
		$this->grocery_crud->set_relation_n_n( 'groups', 'users_groups', 'groups', 'user_id', 'group_id', 'name' );

		$this->grocery_crud->columns('username', 'groups', 'email', 'active', 'last_login');
		$this->grocery_crud->callback_column('last_login',array($this,'timestamp_to_dateformat'));
		
		$this->grocery_crud->set_theme('datatables');
		$this->data['grocery'] = $this->grocery_crud->render();
		$this->data['template'] = 'users';
		$this->index();
	}

	function timestamp_to_dateformat($value, $row)
	{
		return date('r', $value);
	}

	function permalink( $feeditem_id = 0 )
	{
		$this->db->where('feeditem_id', $feeditem_id);
		$query = $this->db->get('feeditemcontents');
		if($query->num_rows() > 0) {
			$row = $query->row();
			$data['content'] = $row->content;
		} else {
			$data['content'] = '<p>empty record</p>';			
		}
		$this->load->view('permalink', $data);
	}
}

/* end of admin.php */