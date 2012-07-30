$(function() {
	$('form').submit(function(e) {
		e.preventDefault();
		var $form = $(e.target);
		var form_action = window.location.origin + $form.attr('action');
		var data_format = $('input:radio[name=data-format]:checked').val();
		if( data_format == 'text' ) {
			var form_data = $form.serialize();
			var content_type = 'application/x-www-form-urlencoded';
		} else {
			var form_data = $form.find(':input').first().val();
			var content_type = 'application/json';			
		}

		$.ajax({
			type: 'post',
			contentType: content_type,
			url: form_action,
			data: form_data,
			success: function(result) {
				$('#response-inner').empty().append(JSON.stringify(result));
			}
		});
	});
});