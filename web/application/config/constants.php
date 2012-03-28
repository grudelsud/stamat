<?php  if ( ! defined('BASEPATH')) exit('No direct script access allowed');

/*
|--------------------------------------------------------------------------
| File and Directory Modes
|--------------------------------------------------------------------------
|
| These prefs are used when checking and setting modes when working
| with the file system.  The defaults are fine on servers with proper
| security, but you may wish (or even need) to change the values in
| certain environments (Apache running a separate process for each
| user, PHP under CGI with Apache suEXEC, etc.).  Octal values should
| always be used to set the mode correctly.
|
*/
define('FILE_READ_MODE', 0644);
define('FILE_WRITE_MODE', 0666);
define('DIR_READ_MODE', 0755);
define('DIR_WRITE_MODE', 0777);

/*
|--------------------------------------------------------------------------
| File Stream Modes
|--------------------------------------------------------------------------
|
| These modes are used when working with fopen()/popen()
|
*/

define('FOPEN_READ',							'rb');
define('FOPEN_READ_WRITE',						'r+b');
define('FOPEN_WRITE_CREATE_DESTRUCTIVE',		'wb'); // truncates existing file data, use with care
define('FOPEN_READ_WRITE_CREATE_DESTRUCTIVE',	'w+b'); // truncates existing file data, use with care
define('FOPEN_WRITE_CREATE',					'ab');
define('FOPEN_READ_WRITE_CREATE',				'a+b');
define('FOPEN_WRITE_CREATE_STRICT',				'xb');
define('FOPEN_READ_WRITE_CREATE_STRICT',		'x+b');

define('BASE_URL', (empty($_SERVER['HTTPS']) ? 'http' : 'https').'://'.$_SERVER['HTTP_HOST'].str_replace('//', '/', dirname($_SERVER['SCRIPT_NAME']).'/'));
define('ASSETS_URL', BASE_URL.'application/public/');

define('READITLATER_API_KEY', '972TpV1eg2b72H40VQdaN70u71paq15f');

define('GROUP_ADMIN', 'admin');
define('HOURLYLIMIT_MEMBERS', 1000);

define('VOCABULARY_SYS_TAGS', 'system');
define('VOCABULARY_TEAMLIFE_SANR', 'teamlife_sanr');
define('VOCABULARY_EXTRACTED_TOPICS', 'topics');
define('VOCABULARY_EXTRACTED_ENTITIES', 'entities');

define('STRUCT_OBJ_FEEDITEM', 'feed-item');
define('STRUCT_ACT_ANNOTATE', 'annotate');

define('STRUCT_ENG_MICCLDA', 'micc-lda');
define('STRUCT_ENG_TEAMLIFE', 'teamlife-sanr');

define('ANNOTATED_MICC', 1);
define('ANNOTATED_SANR', 2);

define('STRUCT_OBJ_KEYWORD', 'keyword');
define('STRUCT_OBJ_TOPIC', 'topic');
define('STRUCT_OBJ_ENTITY', 'entity');

/* End of file constants.php */
/* Location: ./application/config/constants.php */