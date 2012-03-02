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
		load_pagination( selected_feed.id );
		show_feed_items( selected_feed.id, 0, feed_pagesize );
	});

	$('#feed_content .item button.fetch_content').live('click', function() {
		var feeditem_id = $(this).closest('.item').attr('id').replace('item_', '');
		fetch_store_permalink( feeditem_id );
	});

	$('#feed_content .item button.fetch_entities').live('click', function() {
		var data = {};
		data.feeditem_id = $(this).closest('.item').attr('id').replace('item_', '');
		data.annotate_micc = 1;
		data.annotate_teamlife = 0;
		api('fetch_entities', function(data) {
			console.log('silence is bliss');
		}, data);
	});

	$('#feed_content .item button.show_content').live('click', function() {
		var feeditem_id = $(this).closest('.item').attr('id').replace('item_', '');
		$('#feed_content .item').removeClass('selected');
		$('#feed_content #item_'+feeditem_id).addClass('selected');
		var url = base_url + 'index.php/admin/permalink/' + feeditem_id;
		$('#permalink_content').empty().append('<iframe src="'+url+'"></iframe>');
		$('#permalink_container').fadeIn();
	});
	
	$('#permalink_controls button.close').click(function() {
		$('#feed_content .item').removeClass('selected');
		$('#permalink_container').fadeOut();		
	});

	$('#feed_pagination a').live('click', function(e) {
		e.preventDefault();
		$('#feed_pagination a').removeClass('selected');
		$('#feed_pagination a').removeClass('neighbor');
		$(this).addClass('selected');
		$(this).next().addClass('neighbor');
		$(this).prev().addClass('neighbor');
		var page = $(this).attr('id').replace('page_', '');
		show_feed_items( selected_feed.id, page, feed_pagesize );
	});

	$('.check').live('click', function() { $(this).toggleClass('selected'); });
});

function load_pagination( feed_id )
{
	$('#feed_pagination').empty();
	var data = {};
	data.feed_id = feed_id;
	api('count_feed_items', function(data) {
		var paging = 'select page ';
		var side = 2;
		var total = data.success;
		var pages = total / feed_pagesize;
		for( var i = 0; i < pages; i++ ) {
			if( (i < side) || (i > pages - side) || (0 == (i + 1) % 10) ) {
				paging += '<a href="#" id="page_'+i+'" class="marker">'+(i+1)+'</a> ';
			} else {
				paging += '<a href="#" id="page_'+i+'">'+(i+1)+'</a> ';	
			}
		}
		$('#feed_pagination').append( paging );
	}, data);
}

function show_feed_items( feed_id, page, limit )
{
	var data = {};
	data.feed_id = feed_id;
	data.offset = page * limit;
	data.limit = limit;

	api('load_feed_items', function(data) {
		var $feed_content = $('#feed_content');
		$feed_content.empty();
		$.each(data.success, function(key,val) {
			var item_id = 'item_'+val.id;
			var $item = $('<div id="'+item_id+'" class="item"></div>');
			var item_controls = '<div class="loader"></div>';
			item_controls += '<button type="button" class="fetch_entities">[BETA] fetch entities</button> ';
			item_controls += '<button type="button" class="annotate_micc check"></button> MICC ';
			item_controls += '<button type="button" class="annotate_teamlife check"></button> SANR ';
			item_controls += '<button type="button" class="fetch_content">fetch permalink content</button> ';
			item_controls += '<button type="button" class="show_content">show permalink content</button> ';

			$item.append('<div class="item_controls">'+item_controls+'</div>');
			$item.append('<h1><a href="'+val.permalink+'">'+val.title+'</a></h1>');
			$item.append(val.description);
			$item.append('<p class="footer">'+val.date+'</p>');
			$feed_content.append($item);
			if( $.isEmptyObject( val.content_id ) ) {
				$('#'+item_id+' button.show_content').hide();
			} else {
				$('#'+item_id+' button.fetch_content').hide();
			}
		});
	}, data);
}

function fetch_store_permalink( feeditem_id )
{
	$('#permalink_container').fadeOut();
	$('#item_'+feeditem_id+' button.fetch_content').hide();
	$('#item_'+feeditem_id+' div.loader').show();
	var data = {};
	data.feeditem_id = feeditem_id;
	api('fetch_store_permalink', function() {
		$('#item_'+feeditem_id+' div.loader').hide();
		$('#item_'+feeditem_id+' button.show_content').show().click();
	}, data);
}