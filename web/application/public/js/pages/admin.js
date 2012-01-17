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
	feeds_table = $('#feeds_table').dataTable({
		'bProcessing' : true,
		'bJQueryUI' : true,
		'aaData' : data.success,
		'aoColumns' : [
			{ 'mDataProp': 'id' },
			{ 'mDataProp': 'title' },
			{ 'mDataProp': 'url' },
		],
	});
}

function add_feed(data)
{
	var row = {};
	
	$.each(data.success, function(key,val) {
		row.id = val.id;
		row.title = val.title;
		row.url = val.url;
		feeds_table.fnAddData( row );
	});
}