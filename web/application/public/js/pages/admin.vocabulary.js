$(function() {

	api( 'get_vocabulary_tags', append_tags );
	
	$('#form_add_tag').submit(function(e) {
		e.preventDefault();
		var data = $(this).serialize();
		api( 'add_tag', append_tags, data );
	});
	
	$('#vocabulary_detail li').live({
		click: function() {
			$('#parent').val( $(this).html() );
			$('input[name|="parent_id"]').val( $(this).attr('id').replace('tag_', '') );
		},
		mouseover: function() {
			var children = 'child_'+$(this).attr('id').replace('tag_', '');
			$(this).addClass('selected')
			$('#vocabulary_detail li.'+children).addClass('child_over');
			if( $(this).hasClass('child') ) {
				var match = $(this).attr('class').match(/child_([0-9])/i)[1];
				var parent = 'tag_'+match;
				$('#vocabulary_detail li#'+parent).addClass('parent_over');
			}
		},
		mouseout: function() {
			$(this).removeClass('selected')
			$('#vocabulary_detail li').removeClass('parent_over child_over');
		}
	});

	$('#clear').click(function() {
		$('#parent').val( '' );
		$('input[name|="parent_id"]').val( 0 );		
	});
});

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
