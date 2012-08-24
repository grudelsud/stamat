$(function() {

	var mapOptions = {
		center: new google.maps.LatLng(50, -10),
		zoom: 5,
		mapTypeId: google.maps.MapTypeId.ROADMAP
	};

	var map;

	$('#tab_map_link').on('show', function(e) {
		if( map == undefined) {
			map = new google.maps.Map(document.getElementById("map_canvas"), mapOptions);
		}
	});
})