var stamat_api_key = 'rxcvbnoibuvyctxrtcyvbn6oiu';

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
