var stamat_api_key = 'rxcvbnoibuvyctxrtcyvbn6oiu';

$(function() {
	$('#dialog').dialog({autoOpen: false, resizable: false, height:140, modal: true});
	
	api( 'get_vocabulary_tags', append_tags );
	
	$('#vocabulary_detail #root li, #vocabulary_detail #children li').live({
		click: function() {
			$(this).toggleClass('selected');
			$('#parent').val( $(this).html() );
			$('input[name|="parent_id"]').val( $(this).attr('id').replace('tag_', '') );
		},
		mouseover: function() {
			var children = 'child_'+$(this).attr('id').replace('tag_', '');
			$('#vocabulary_detail li.'+children).addClass('child_over');

			if( $(this).hasClass('child') ) {
				var match = $(this).attr('class').match(/child_([0-9])/i)[1];
				var parent = 'tag_'+match;
				$('#vocabulary_detail li#'+parent).addClass('parent_over');
			}
		},
		mouseout: function() {
			$('#vocabulary_detail #root li, #vocabulary_detail #children li').removeClass('parent_over child_over');
		}
	});

});
// used both by admin.feed and admin.vocabulary
function append_tags(data)
{
	var $list_root = $('#root');
	var $list_children = $('#children');
	$.each(data.success, function(key, val) {
		if( val.parent_id == null ) {
			$list_root.append('<li id="tag_'+val.id+'">'+val.name+'</li>');
		} else {
			$list_children.append('<li id="tag_'+val.id+'" class="child child_'+val.parent_id+'">'+val.name+'</li>');
		}
	});
}

function show_error_message( message ) {
	$('#dialog').empty().append( message ).dialog('option', {
		'title': 'error',
		'buttons': {
			'ok': function() { $(this).dialog('close'); }
		}
	}).dialog('open');
}

// make an api call
function api(method, callback, data) {
	if( typeof data == 'object' ) {
		data.access_key = stamat_api_key;		
	} else if( typeof data == 'string' ) {
		data += '&access_key=' + stamat_api_key;
	} else {
		data = {};
		data.access_key = stamat_api_key;		
	}
	$.ajax({
		cache    : false,
		data     : data,
		dataType : 'JSON',
		success  : callback,
		type     : 'POST',
		url      : '/api/'+method
	});
}
