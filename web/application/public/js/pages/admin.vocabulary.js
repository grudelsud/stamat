$(function() {

	api( 'get_vocabulary_tags', load_tags );
	
	$('#form_add_tag').submit(function(e) {
		e.preventDefault();
		var data = $(this).serialize();
		api( 'add_tag', add_tag, data );
	});

});

function load_tags(data)
{
	console.log(data);
}

function add_tag(data)
{
	console.log(data);
}
