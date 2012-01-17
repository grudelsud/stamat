<!DOCTYPE html>
<html>
	<head>
		<?php $this->load->view('assets'); ?>
		<script type="text/javascript" src="<?php echo ASSETS_URL; ?>js/pages/admin.<?php echo $template; ?>.js"></script>
	</head>
	<body id="admin" class="<?php echo $template; ?>">
		<?php $this->load->view('admin/header'); ?>
		<?php $this->load->view('admin/'.$template); ?>
	</body>
</html>