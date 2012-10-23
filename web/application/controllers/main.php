<?php if ( ! defined('BASEPATH')) exit('No direct script access allowed');

class Main extends CI_Controller {

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

		
		if ($this->session->userdata('detail_needed')){
                    
                    
                    $this->data['template'] = 'user_detail';
                    
                    $this->load->library('form_validation');
                    
                    $this->data['first_name'] = array('name' => 'first_name',
				'id' => 'first_name',
				'type' => 'text',
				'value' => $this->form_validation->set_value('first_name'),
			);
			$this->data['last_name'] = array('name' => 'last_name',
				'id' => 'last_name',
				'type' => 'text',
				'value' => $this->form_validation->set_value('last_name'),
			);
			$this->data['email'] = array('name' => 'email',
				'id' => 'email',
				'type' => 'text',
				'value' => $this->form_validation->set_value('email'),
			);
			
			$this->data['password'] = array('name' => 'password',
				'id' => 'password',
				'type' => 'password',
				'value' => $this->form_validation->set_value('password'),
			);
			$this->data['password_confirm'] = array('name' => 'password_confirm',
				'id' => 'password_confirm',
				'type' => 'password',
				'value' => $this->form_validation->set_value('password_confirm'),
			);

                    
                }else{
                    $this->data['template'] = 'home';
                }
                
		/**
		 * controlla che l'utente abbia i dati personali completi, se sono completi allora carica home
		 * altrimenti carica il template user_detail (dentro views/main)
		 * $this->data['template'] = 'user_detail';
		 */
	}

	function index()
	{
                $this->session->set_userdata('detail_needed', false);
		if ( !$this->logged_in ) {
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('main_template', $this->data);
	}

	function update_user() {
		// fai tutte le tue belle cosine sul database e poi
            
             
            //$this->data['title'] = "Create User";
            
            $this->load->library('form_validation');
            
            //validate form input
            $this->form_validation->set_rules('first_name', 'First Name', 'required|xss_clean');
            $this->form_validation->set_rules('last_name', 'Last Name', 'required|xss_clean');
            $this->form_validation->set_rules('email', 'Email Address', 'required|valid_email');
            $this->form_validation->set_rules('password', 'Password', 'required|min_length[' . $this->config->item('min_password_length', 'ion_auth') . ']|max_length[' . $this->config->item('max_password_length', 'ion_auth') . ']|matches[password_confirm]');
            $this->form_validation->set_rules('password_confirm', 'Password Confirmation', 'required');

		if ($this->form_validation->run() == true)
		{
			$username = strtolower($this->input->post('first_name')) . ' ' . strtolower($this->input->post('last_name'));
			$email = $this->input->post('email');
			$password = $this->input->post('password');

			$additional_data = array('first_name' => $this->input->post('first_name'),
				'last_name' => $this->input->post('last_name'),
			);
                        $id = $this->session->userdata('user_id');
                        $data = array(
					'email' => $email,
					'password' => $password,
					 );
                        $this->ion_auth->update($id, $data);
                        $this->logged_in = $this->ion_auth->login($email, $password);
                        redirect('/', 'refresh');
		}
                
                /*
		if ($this->form_validation->run() == true && $this->ion_auth->register($username, $password, $email, $additional_data))
		{ //check to see if we are creating the user
			//redirect them back to the admin page
                        $this->logged_in = $this->ion_auth->login($email, $password);
			$this->session->set_flashdata('message', "User Created");
			redirect('/', 'refresh');
        	}*/
		else
		{ //display the create user form
			//set the flash data error message if there is one
			$this->data['message'] = (validation_errors() ? validation_errors() : ($this->ion_auth->errors() ? $this->ion_auth->errors() : $this->session->flashdata('message')));

			$this->data['first_name'] = array('name' => 'first_name',
				'id' => 'first_name',
				'type' => 'text',
				'value' => $this->form_validation->set_value('first_name'),
			);
			$this->data['last_name'] = array('name' => 'last_name',
				'id' => 'last_name',
				'type' => 'text',
				'value' => $this->form_validation->set_value('last_name'),
			);
			$this->data['email'] = array('name' => 'email',
				'id' => 'email',
				'type' => 'text',
				'value' => $this->form_validation->set_value('email'),
			);
			
			$this->data['password'] = array('name' => 'password',
				'id' => 'password',
				'type' => 'password',
				'value' => $this->form_validation->set_value('password'),
			);
			$this->data['password_confirm'] = array('name' => 'password_confirm',
				'id' => 'password_confirm',
				'type' => 'password',
				'value' => $this->form_validation->set_value('password_confirm'),
			);
			$this->load->view('main/user_detail', $this->data);
		}
        
        
		redirect('/', 'refresh');
	}

	// this clearly is not the right place for this function, it's just that I don't want to add code to someone else's classes for maintenance (ion_auth in this case)
	function login_facebook()
	{
		$config = array(
			'appId' => FB_APP_ID,
			'secret' => FB_APP_SECRET,
			'fileUpload' => TRUE
		);
		$this->load->library('Facebook', $config);
		$user = $this->facebook->getUser();

		$debug = '';
		$profile = null;
		if( $user ) {
			try {
				// Proceed knowing you have a logged in user who's authenticated.
				$profile = $this->facebook->api('/me?fields=id,name,link,email');
				$this->load->library('ion_auth');
                                
				$login = $this->ion_auth->login($profile['email'], $profile['id']);

				if( !$login ) {
					$this->ion_auth->register($profile['name'], $profile['id'], $profile['email']);
					$this->logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
				}
			} catch (FacebookApiException $e) {
				// TODO: I'm sure we should do something here
			}
		}
		// echo $debug;
		redirect('/', 'refresh');
	}

        // function to allow users to log in via twitter
	function login_twitter()
	{
		$config = array(
			'consumer_key'  => TWITTER_COSUMERKEY, 
			'consumer_secret' => TWITTER_CONSUMERSECRET,
			'oauth_token' => NuLL,
			'oauth_token_secret' => NULL);

		$this->load->library('Twitteroauth',$config);

		// Requesting authentication tokens, the parameter is the URL we will be redirected to
		$request_token = $this->twitteroauth->getRequestToken(BASE_URL . 'index.php/main/oauth_twitter');

		// If everything goes well..  
		if($this->twitteroauth->http_code==200){  
		// Let's generate the URL and redirect  
			$url = $this->twitteroauth->getAuthorizeURL($request_token['oauth_token']); 
			   
			$this->load->library('session');
			$this->session->set_userdata('oauth_token', $request_token['oauth_token']);
			$this->session->set_userdata('oauth_token_secret', $request_token['oauth_token_secret']);

			redirect($url, 'refresh');

		} else { 
			// It's a bad idea to kill the script, but we've got to know when there's an error.  
			die('Something wrong happened.');  
		}  

	}

	function oauth_twitter()
	{
		$cosumerKey = TWITTER_COSUMERKEY;
		$cosumerSecret = TWITTER_CONSUMERSECRET;

		$oauth_token = $this->session->userdata('oauth_token');
		$oauth_token_secret = $this->session->userdata('oauth_token_secret');

		if(!empty($_GET['oauth_verifier']) && $oauth_token!== false && $oauth_token_secret!== false){

			$config = array(
				'consumer_key'  => TWITTER_COSUMERKEY, 
				'consumer_secret' => TWITTER_CONSUMERSECRET,
				'oauth_token' => $oauth_token,
				'oauth_token_secret' => $oauth_token_secret);

			$this->load->library('Twitteroauth',$config);
                        // Let's request the access token  
			$access_token = $this->twitteroauth->getAccessToken($_GET['oauth_verifier']); 
			// Save it in a session var 
			$this->session->set_userdata('access_token', $access_token);
			// Let's get the user's info 
			$user_info = $this->twitteroauth->get('account/verify_credentials'); 
			
                        $screen_name=$user_info->screen_name;
			
                        $profile = null;
			$profile['name']=$user_info->screen_name;
			$profile['email']=$user_info->screen_name . '@twitter.com';
			$profile['id'] = $user_info->id;            
                        $additional_info = array(
                                'screen_name' => $screen_name,
				'oauth_token' => $oauth_token,
				'oauth_token_secret' => $oauth_token_secret);
                        
                   	
                        $this->tables  = $this->config->item('tables', 'ion_auth');
                        $query = $this->db->select('username, email, id, password, active, last_login')
		                  ->where(sprintf("(screen_name = '%1\$s')", $this->db->escape_str($screen_name)))
		                  ->limit(1)
		                  ->get($this->tables['users']);

                        $user = $query->row();
                        
                        
                	
                        if ($query->num_rows() == 1) {
                            $session_data = array(
                                'username'             => $user->username,
                                'email'                => $user->email,
                                'user_id'              => $user->id, //everyone likes to overwrite id so we'll use user_id
                                'old_last_login'       => $user->last_login
                            );

                            $this->session->set_userdata($session_data);
                            
                        }else{
                        
                            
                            
                            $this->session->set_userdata('detail_needed', true);
                            /*
                            $session_data = array(
                                'username'             => $screen_name,
                                 'user_id'             => $user_info->id
                                 );
                            $this->session->set_userdata($session_data);
                            */
                            
                            $this->ion_auth->register($profile['name'], $profile['id'], $profile['email'],$additional_info);
                            $this->logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
                        }
                            
                        /*
                        $login=false;
                        
			if( !$login ) {
				$this->ion_auth->register($profile['name'], $profile['id'], $profile['email'],$additional_info);
				$this->logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
			}
                        */
                        
                        /* code to extract the timeline and the entities
                        $home_timeline = $this->twitteroauth->get('statuses/home_timeline',array('count' => 200)); 
			//print_r($home_timeline);
                        $timeline_content = null;
                        foreach ($home_timeline as &$tweet) {
                            $timeline_content = $timeline_content . $tweet->text;
                        }
                        
                        $this->load->model('scraper_model');
                        $entities=$this->scraper_model->scrape_stamat_ner( $timeline_content);
                        
                         */
                        redirect('/', 'refresh');

		} else {  
			// Something's missing, go back to square 1  
			redirect('/', 'refresh');  
		}  
	}
}

/* End of main.php */
