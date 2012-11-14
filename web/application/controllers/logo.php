<?php defined('BASEPATH') OR exit('No direct script access allowed');

class logo extends CI_Controller {

	function __construct()
	{
		parent::__construct();
                $this->load->library('ion_auth');
		$this->load->library('session');
		$this->load->database();
		$this->load->helper('url');
	}

	//redirect if needed, otherwise display the user list
	function index()
	{
		if (!$this->ion_auth->logged_in())
		{
			//redirect them to the login page
			redirect('auth/login', 'refresh');
		}
		else
		{
                	$this->load->view('logo/index');
		}
	}
        
       
        
        public function upload()
        {
            error_reporting(E_ALL | E_STRICT);

            
            
            $data = $this->input->post('newsletter');
        
            $this->load->helper("upload.class");
        
            if (isset($_REQUEST['form_type'])) {
                $form_type = $_REQUEST['form_type'];
            } else {
                $form_type = "";
            }
        
            $upload_handler = new UploadHandler();
            $upload_handler->setPath($form_type);
        
        
            header('Pragma: no-cache');
            header('Cache-Control: no-store, no-cache, must-revalidate');
            header('Content-Disposition: inline; filename="files.json"');
            header('X-Content-Type-Options: nosniff');
            header('Access-Control-Allow-Origin: *');
            header('Access-Control-Allow-Methods: OPTIONS, HEAD, GET, POST, PUT, DELETE');
            header('Access-Control-Allow-Headers: X-File-Name, X-File-Type, X-File-Size');
            switch ($_SERVER['REQUEST_METHOD']) {
            case 'OPTIONS':
                break;
            case 'HEAD':
            case 'GET':
                $upload_handler->get();
                break;
            case 'POST':
                if (isset($_REQUEST['_method']) && $_REQUEST['_method'] === 'DELETE') {
                    $upload_handler->delete();
                    $file_path = $upload_handler->getFullFilenameUrlDelete();
                    
                    if ($form_type ==1){
                        $queryString ='DELETE FROM logo WHERE url="'. $file_path . '"';
                        $query=$this->db->query($queryString);
                    }
                    elseif ($form_type ==2)   {
                        $queryString ='DELETE FROM video WHERE url="'. $file_path . '"';
                        $query=$this->db->query($queryString);
                    }
                    
                } else {
                    $upload_handler->post();
                    $urlString=$upload_handler->getFullFilenameUrl();
                    $pathString = $upload_handler->getFullFilename();
                    
                    if ($form_type ==1){
                        $queryString = 'INSERT INTO logo (url,path) VALUES("'. $urlString . '" , "' . $pathString . '")';
                        $query=$this->db->query($queryString);
                    }
                    elseif ($form_type ==2)   {
                        $queryString = 'INSERT INTO video (url,path) VALUES("'. $urlString . '" , "' . $pathString . '")';
                        $query=$this->db->query($queryString);
                    }
                }
                break;
            case 'DELETE':
                $upload_handler->delete();
                break;
            default:
                header('HTTP/1.1 405 Method Not Allowed');
        }

    }
    
    public function process(){
        
        $urls = $this->input->post('urls');
        $urlsArray= explode (",",$urls);
        $imageUrl = $urlsArray[0];
        $videoUrl = $urlsArray[1];
        $queryString = "SELECT id, path FROM logo WHERE  url='" .$imageUrl. "'";
        $query=$this->db->query($queryString);
        $row = $query->result();
        $id_logo = $row[0]->id;
        $path_logo = $row[0]->path;
        
        $queryString = "SELECT id, path FROM video WHERE  url='" .$videoUrl. "'";
        $query=$this->db->query($queryString);
        $row = $query->result();
        $id_video = $row[0]->id;
        $path_video = $row[0]->path;
        
        
        $match_params = "-S -e SIFT -d PyramidSIFT -m FlannBased -v -f 2  -R -s ";
        $result_path = "../test/results/";
        $LOGORECOG_BIN = "/home/ubuntu/STAMAT/logorecog-tmp/src/bresci";

        $execLogoString = $LOGORECOG_BIN . "-q  ". $path_logo . " -V " . $path_video . " " . $match_params . " " .$result_path ;
        
        
        //$execString = "java -jar /Users/serra/git/stamat/web/application/public/scripts/logoDetectionOnly.jar " . $imageUrl . " " . $videoUrl . ' > /dev/null &';
        $execString = "java -jar /Users/serra/git/stamat/web/application/public/scripts/waitTime.jar " . $path_logo . " " . $path_video ." > /dev/null 2>&1 & echo $!; " ;
        //exec($execString,$output,$returnValue);
        
        $cvsFile ="/Users/serra/git/stamat/web/application/public/logoResults/test.csv";
        $javaCVSreader ="java -jar /Users/serra/git/stamat/web/application/public/scripts/logoDetectionCSV.jar " . $imageUrl . " " . $videoUrl . " " . $cvsFile;
        $execString = $execString . $javaCVSreader;
        //xdebug_break();
        $queryString = 'INSERT INTO process (name, idProcessStatus, id_logo, id_video, command) VALUES("Logo Detection",1,'. $id_logo . ',' . $id_video . ',"'.$execString.'")';
        $query=$this->db->query($queryString);
        
    }
    
    public function checkStatus(){
        
        $this->load->database();
        $queryString = "SELECT process.idProcessNum AS idProcess, logo.url AS logoUrl, video.url AS videoUrl, processstatus.name As status, process.detection 
                        FROM process JOIN logo ON process.id_logo = logo.id JOIN video ON process.id_video = video.id JOIN processstatus on processstatus.idProcessStatus = process.idProcessStatus";
        $query=$this->db->query($queryString);
        $num=$query->num_rows();
        $results= array();
        foreach ($query->result() as $row)
        {
            $scoreFrame=0.75;
            $scoreVideo=5;
            $queryString="SELECT numFrame FROM processResults WHERE idProcessNum =" .$row->idProcess ." AND score >= ". $scoreFrame;
            $query=$this->db->query($queryString);
            $num=$query->num_rows();
            if ($num>=$scoreVideo){
                $detection ="true";
            }
            else {
                $detection="false";
            }
            $resultRow = array(
               'idProcess' => $row->idProcess,
               'logoUrl'  => $row->logoUrl,
               'videoUrl' => $row->videoUrl,
               'status' => $row->status,
               //'detection' => $row->detection
               'detection' => $detection
               );
            array_push($results,$resultRow);
            
        }
        return  $results;
   }
   
       public function checkStatusJSON(){
        $results=$this->checkStatus();
        $this->output->set_content_type('application/json')
                 ->set_output(json_encode($results));
        
   }

	
}
