<div id="header">
	<div id="logo"><h1>REACT</h1></div>
	<div id="navigation">
		<p>howdy, <?php echo $logged_user['username']; ?>!</p>

		<div id="menu_main">
			<ul>
				<li><a href="<?php echo site_url('/auth/logout'); ?>">logout</a></li>
			</ul>
		</div>
		<?php if( !empty($logged_admin) && $logged_admin ) : ?>
		<div id="menu_admin">
			<ul>
				<li><a href="<?php echo site_url('/admin'); ?>">admin</a></li>
				<li><a href="<?php echo site_url('/tools'); ?>">tools</a></li>
			</ul>
		</div>
		<?php endif; ?>
	</div>
</div>