<!DOCTYPE html>
<html>
	<head>
		<?php $this->load->view('assets'); ?>
	</head>
	<body id="admin" class="<?php echo $template; ?>">
		<?php $this->load->view('admin/header'); ?>
		<?php $this->load->view('admin/'.$template); ?>
	</body>
</html>