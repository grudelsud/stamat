$(function() {
	$('#fb_login').click(function() {
		FB.login(function(response) {
			if (response.authResponse) {
				console.log('Welcome!  Fetching your information.... ');
				FB.api('/me', function(response) {
					window.location = base_url + 'index.php/main/login_facebook';
				});
			} else {
				console.log('User cancelled login or did not fully authorize.');
			}
		}, {scope: 'email'});
	});
});