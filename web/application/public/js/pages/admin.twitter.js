$(function() {    
    // server replies with data.success objects containing
    // data.success.text - text of selected tweets
    // data.success.images_urls - array of urls for selected tweets
    $('#form_search_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
                api('search_tweets', load_tweets,data);
    });   
});    

// load tweet content in tweets table
function load_tweets(data){ 
      tweets_table = $('#tweets_table').dataTable({
		'bDestroy': true,   // new table for each search
                'bProcessing' : true,
		'bJQueryUI' : true, 
                'iDisplayLength': 50,
                'sPaginationType': 'full_numbers', 
                'aaData': data.success, 
		'aoColumns': [
                    { 'mDataProp': "text"},
                    { 'mDataProp': "images_urls.0",  
                      'sDefaultContent': '  ---   '
                    },	
		],
                'fnRowCallback': load_Images_RowCallback
	});
}
// generate img tags at render time
function load_Images_RowCallback( nRow, aData, iDisplayIndex ){  // aData is the data.success current element
    $.each(aData['images_urls'], function(key, val) {
	$('td:eq(1)', nRow).html( '<a href="' + aData['images_urls'][key] + '"><img src="' + aData['images_urls'][key] + '" /></a>' );	
	});
    return nRow;
}


