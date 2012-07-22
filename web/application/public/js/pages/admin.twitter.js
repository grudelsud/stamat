$(function() {    
    // server replies with data.success objects containing
    // data.success.text - text of selected tweets
    // data.success.images_urls - array of urls for selected tweets
    $('#form_search_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
                // start the loader icon under the search button
                $('#search_tweets').append('<div class="loader"></div>');
                $('#search_tweets'+' div.loader').show();
                $('#search_tweets'+' div.form_search_tweets').fadeOut();
                // disable search button while server is processing
                $('input[type=submit]', this).attr('disabled', 'disabled');
                api('search_tweets', function(result){
                    load_tweets(result);
                    // stop loader icon and fadeIn search button
                    $('#search_tweets'+' div.loader').hide();
                    $('#search_tweets'+' div.form_search_tweets').fadeIn();
                    $('input[type="submit"]').removeAttr('disabled');
                    $('#search_tweets'+' div.loader').remove();
                }
                ,data);
    });   
    $('#twitter-tab-content a[href="#tweet_section"]').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
    });
    $('#twitter-tab-content a[href="#cluster_section"]').click(function (e) {
        e.preventDefault();
        $(this).tab('show');
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
                    {'mDataProp': "created_at","asSorting": [ "desc" ]},   // displaing ordered tweets, by timestamp
                    {'mDataProp': "text"},
                    {'mDataProp': "images_urls.0",  
                      'sDefaultContent': '  ---   '
                    },	
		],
                'fnRowCallback': load_Images_RowCallback
	});
}
// generate img tags at render time; 
 function load_Images_RowCallback( nRow, aData, iDisplayIndex ){  // aData is the data.success current element
    $.each(aData['images_urls'], function(key, val) {
	$('td:eq(2)', nRow).html('<div class="tweet_img">'+'<a href="'+aData['images_urls'][key]+
            '"><img src="'+aData['images_urls'][key]+'"'+' style="height:180px; width: 280px"'+'></a>'+'</div>');	
	});
    return nRow;
}


//