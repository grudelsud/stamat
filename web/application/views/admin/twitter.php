
<div class="row">
	<div id="tweet_container" class="span8">
		<h3>manage tweets</h3>
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