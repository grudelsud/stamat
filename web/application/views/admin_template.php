<!DOCTYPE html>
<html>
	<head>
		<link href='http://fonts.googleapis.com/css?family=Magra:400,700' rel='stylesheet' type='text/css'>
		<link rel="stylesheet" type="text/css" media="all" href="<?php echo ASSETS_URL; ?>css/style.css" />
		
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/modernizr.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/jquery-1.7.1.min.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/all.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/admin.js"></script>
	</head>
	<body id="admin" class="<?php echo $template; ?>">
		<?php $this->load->view('admin/header'); ?>
		<?php $this->load->view('admin/'.$template); ?>
	</body>
</html>