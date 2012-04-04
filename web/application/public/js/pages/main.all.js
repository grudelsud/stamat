$(function() {
	$(window).scroll(function() {
		var scroll = $(this).scrollTop() + 'px';
		$('.scroll-top').css('padding-top', scroll);
	});
});