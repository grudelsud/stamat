<!DOCTYPE html>
<html>
	<head>
		<?php $this->load->view('assets'); ?>
		<link rel="stylesheet" type="text/css" media="all" href="<?php echo ASSETS_URL; ?>css/admin.style.css" />
	</head>
	<body id="admin">
		<div id="header">
			<div id="logo"><h1><a href="<?php echo site_url('/admin/feed'); ?>">STAMAT - Social Topics and Media Analysis Tool</a></h1></div>
			<ul>
				<li><a href="<?php echo site_url('/tools/create_slugs'); ?>">create / refresh all slugs</a></li>
				<li><a href="<?php echo site_url('/tools/create_scrapers'); ?>">reset all scrapers</a></li>
				<li><a href="<?php echo site_url('/auth/logout'); ?>">logout</a></li>
			</ul>
		</div>
		<div id="content">
			<?php echo $output; ?>
		</div>
		<div id="dialog"></div>
		<?php $this->load->view('admin/footer'); ?>
	</body>
</html>