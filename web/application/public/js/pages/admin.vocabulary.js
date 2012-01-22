$(function() {

	$('#form_add_tag').submit(function(e) {
		e.preventDefault();
		var data = $(this).serialize();
		api( 'add_tag', append_tags, data );
	});
	
	$('#clear').click(function() {
		$('#parent').val( '' );
		$('input[name|="parent_id"]').val( 0 );		
	});
});
