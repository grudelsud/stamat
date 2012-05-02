$(function() {    
    
    $('#form_search_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
		api( 'search_tweets', load_tweets, data ); 
    });
     $('#form_show_tweets').submit(function(e) { 
		e.preventDefault();      
		var data = $(this).serialize();
		api( 'get_tweets', add_tweets, data ); 
    });
    
});    

function add_tweets(data) 
{       tweets_table = $('#tweets_table').dataTable({
		'bDestroy': true,   // ad ogni click, ricreo una nuova tabella...
                'bProcessing' : true,
		'bJQueryUI' : true,     // attiva il themeroller jquery UI
		'aaData' : data.success,  // un array di dati, passato durante l'inizializzazione
		'aoColumns' : [
		{ 'mDataProp': 'text' },
		{ 'mDataProp': 'image' },			
		],
	});
        
        var row = {};
	        
	$.each(data.success, function(key,val) {  // key = index, val=element infatti usa solo val sotto
		row.text = val.text;
		row.image = val.url;	
		tweets_table.fnAddData( row );
	});
}
// load tweet content in tweets table
function load_tweets()
{
	
}

