<div class="row">
	<div class="span4" id="auth_traditional">
		<?php $this->load->view('auth/login'); ?>
	</div>

	<div class="span4">
		<div id="auth_fb">
			<button id="fb_login" type="button">Login with Facebook</button>
			<div id="auth_fb_message"></div>
		</div>
		<div id="auth_twitter">
			<button id="twitter_login" type="button">Login with Twitter</button>
		</div>
		<div id="auth_google">
			<p>Login with Google</p>
		</div>
	</div>

	<div class="span4" id="mobile_apps">
		<p>Mobile App</p>
		<div id="app_android"><p>Android</p></div>
		<div id="app_ios"><p>iPhone / iPad</p></div>
	</div>
</div>