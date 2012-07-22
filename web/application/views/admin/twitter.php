
<ul class="nav nav-tabs" data-tabs="tabs">
            <li class="active"><a href="#tweet_section" data-toggle="tab">Twitter Section</a></li>
            <li><a href="#cluster_section" data-toggle="tab">Image Clustering Section</a></li>
</ul>
<div id="twitter-tab-content" class="tab-content">
    
    <div class="tab-pane" id="tweet_section">
        <div class="row">
            <div id="tweet_container" class="span8">
                    <h3>Manage Tweets</h3>
                    <div id="search_tweets"> 
			<?php echo form_open( 'search_tweets', array('id' => 'form_search_tweets') ); ?>
                        <?php echo form_label( 'Key Word to search and store', 'key word' ); ?>
                        <?php echo form_input( array('name'=>'key word', 'id'=>'key word') ); ?>
			
                        <?php echo form_submit( 'submit', 'search tweets' ); ?>
			<?php echo form_close(); ?>
                    </div>
            
                    <div id="tweets">
			<table width="100%" id="tweets_table">
				<thead>
					<tr>
                                                <th width="10%">Date</th>
                                                <th width="10%">Text</th>
						<th width="35%">Images</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
                    </div>
                                             
            </div>
	    
        </div>
    </div>
    
    <div class="tab-pane" id="cluster_section">
        <div class="row">
            <div id="cluster_container" class="span8">
                    <h3>Manage Clustering</h3>
                    
                    <div id="image_clusters">
			<table width="100%" id="clusters_table">
				<thead>
					<tr>
                                                <th width="10%">Cluster Number</th>
						<th width="35%">Images</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
                    </div>
                                             
            </div>
	    
        </div>
          
    </div>
</div>