$(function() {
	api( 'get_feeds', load_feeds );
	
	$('#form_add_feed').submit(function(e) {
		e.preventDefault();
		var data = $(this).serialize();
		api( 'add_feed', add_feed, data );
	});
});

function load_feeds(data)
{
	console.log(data);
}

function add_feed(data)
{
	console.log(data);
}