$(function() {
	$('form').submit(function(e) {
		e.preventDefault();
		var $form = $(e.target);
		var form_action = window.location.origin + $form.attr('action');
		var form_data = $form.serialize();

		$.ajax({
			type: 'post',
			url: form_action,
			data: form_data,
			success: function(result) {
				$('#response-inner').empty().append(JSON.stringify(result));
			}
		});
	});
});