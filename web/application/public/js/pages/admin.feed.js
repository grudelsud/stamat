$(function() {
	api( 'get_feeds', load_feeds );
	
	$('#form_add_feed').submit(function(e) {
		e.preventDefault();
		var data = $(this).serialize();
		api( 'add_feed', add_feed, data );
	});

	$("#feeds_table tbody").click(function(e) {
		$(feeds_table.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(e.target.parentNode).addClass('row_selected');
		var position = feeds_table.fnGetPosition( e.target.parentNode );
		var feed = feeds_table.fnGetData( position );

		$('#feed_detail').hide();
		show_feed_details( feed.id );
	});
});

function show_feed_details( id )
{
	api( 'get_feed', function(data) {
		var $feed_detail = $('#feed_detail');
		if( typeof data.success.feed != 'undefined' ) {
			$feed_content = $('#feed_content');
			$feed_content.empty().append('<h1>'+data.success.feed[0].title+'</h1><p>'+data.success.feed[0].url+'</p>');
			$tags = $('<ul></ul>');
			$.each(data.success.tags, function(key,val) {
				$tags.append('<li>'+val+'</li>');
			});
			$feed_content.append($tags);
			$feed_detail.show();
		}
	}, 'feed_id='+id );
}

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