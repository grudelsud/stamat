$(function() {    
    
    $('#form_search_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
		var message = 'Do you want to store your requested tweets? ';
                        $('#dialog').empty().append( message ).dialog('option', {
                            'title': 'Attention',
                            'buttons': {
                                'ok': function(){
                                   $(this).dialog('close');
                                   store_tweets(data);
                                },  
                                'cancel': function() { $(this).dialog('close'); }
                            }
                }).dialog('open');
    });
    
    $('#form_show_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
		api( 'get_tweets',load_tweets , data ); 
    });
    
});    

// load tweet content in tweets table
function load_tweets(data) 
{       tweets_table = $('#tweets_table').dataTable({
		'bDestroy': true,   // ad ogni click, ricreo una nuova tabella...
                'bProcessing' : true,
		'bJQueryUI' : true, 
                'iDisplayLength': 50,
                'sPaginationType': 'full_numbers', 
                'aaData' : data.success, 
		'aoColumns' : [
		{ 'mDataProp': 'text',
                    'sWidth': '50%',
                    'sTitle': 'Text of tweet', 
                    'sScrollX': '100%'
                },
		{ 'mDataProp': 'url', 
                    'sWidth': '50%', 
                    'sTitle': 'Image from tweet',
                    'sDefaultContent': '  ---NO IMAGES FOUNDED---   '
                },			
		],
	});
}
function store_tweets(data){
    
     api( 'search_tweets', function(){} , data );
}
