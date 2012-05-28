$(function() {
	$('#tag_directory li a.btn').live('click', function() {
		$('#tag_directory li a.btn').removeClass('btn-primary');
		$(this).addClass('btn-primary');
		$('#tag_directory li ul').addClass('hidden');
		$(this).parent().find('ul').removeClass('hidden');
	});
})