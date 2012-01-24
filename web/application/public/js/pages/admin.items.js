$(function() {
	// on page ready load feeds in feed table
	api( 'get_feeds', load_feeds );
	
	// on feed table row click show feed details below table
	$("#feeds_table tbody").click(function(e) {
		$(feeds_table.fnSettings().aoData).each(function () {
			$(this.nTr).removeClass('row_selected');
		});
		$(e.target.parentNode).addClass('row_selected');
		var position = feeds_table.fnGetPosition( e.target.parentNode );
		var feed = feeds_table.fnGetData( position );

		selected_feed.id = feed.id;
		selected_feed.url = feed.url;
		selected_feed.position = position;

		$('#feed_detail').hide();
		show_feed_items( selected_feed.url );
	});

});

function show_feed_items( url )
{
	var data = {};
	data.url = url;
	api('load_feed_items', function(data) {
		var $feed_content = $('#feed_content');
		$feed_content.empty();
		$.each(data.success, function(key,val) {
			var $item = $('<div id="item"></div>');
			$item.append('<h1><a href="'+val.permalink+'">'+val.title+'</a></h1>');
			$item.append(val.description+'<p class="footer">'+val.date+'</p>');
			$feed_content.append($item);
		});
	}, data);
}