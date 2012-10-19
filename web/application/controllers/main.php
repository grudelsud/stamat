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
		$this->data['template'] = 'home';
	}

	function index()
	{
		if ( !$this->logged_in ) {
			redirect('/auth/login', 'refresh');
		}
		$this->load->view('main_template', $this->data);
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
			// Print user's info  
			//print_r($user_info); 
			$profile = null;
			$profile['name']=$user_info->screen_name;
			$profile['email']=$user_info->screen_name . '@twitter.com';
			$profile['id'] = $user_info->id;            
                        $twitter_token = array(
				'oauth_token' => $oauth_token,
				'oauth_token_secret' => $oauth_token_secret);
                        
                   
			
                        $this->tables  = $this->config->item('tables', 'ion_auth');
                        $query = $this->db->select('username, email, id, password, active, last_login')
		                  ->where(sprintf("(username = '%1\$s' OR email = '%1\$s')", $this->db->escape_str($profile['name'])))
		                  ->limit(1)
		                  ->get($this->tables['users']);

                        $user = $query->row();
                        xdebug_break();
                        // my problem
                        $login = $this->ion_auth->login($user->username, 'password');
                	
			if( !$login ) {
				$this->ion_auth->register($profile['name'], $profile['id'], $profile['email'],$twitter_token);
				$this->logged_in = $this->ion_auth->login($profile['email'], $profile['id']);
			}

                        $home_timeline = $this->twitteroauth->get('statuses/home_timeline',array('count' => 200)); 
			print_r($home_timeline);
                        $timeline_content = null;
                        foreach ($home_timeline as &$tweet) {
                            $timeline_content = $timeline_content . $tweet->text;
                        }
                        
                        $this->load->model('scraper_model');
                        $entities=$this->scraper_model->scrape_stamat_ner( $timeline_content);
                        redirect('/', 'refresh');

		} else {  
			// Something's missing, go back to square 1  
			redirect('/', 'refresh');  
		}  
	}
}

/* End of main.php */
