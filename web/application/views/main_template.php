<!DOCTYPE html>
<html>
	<head>
		<?php $this->load->view('assets'); ?>
		<link rel="stylesheet" type="text/css" media="all" href="<?php echo ASSETS_URL; ?>css/main.style.css" />
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/underscore-min.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/backbone-min.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/main.<?php echo $template; ?>.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/main.all.js"></script>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/app/app.js"></script>
	</head>
	<body id="main" class="<?php echo $template; ?>">
		<div id="fb-root"></div>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/fb.init.js"></script>
		<div class="wrapper"><?php $this->load->view('main/header'); ?></div>
		<div class="wrapper"><?php $this->load->view('main/'.$template); ?></div>
		<div class="wrapper"><?php $this->load->view('main/footer'); ?></div>
		<div id="dialog"></div>
	</body>
</html>