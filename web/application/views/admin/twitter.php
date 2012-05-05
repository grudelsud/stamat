
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
                <div id="show_tweets"> 
			<?php echo form_open( 'show_tweets', array('id' => 'form_show_tweets') ); ?>
                        <?php echo form_label( 'Key Word to show', 'key word' ); ?>
                        <?php echo form_input( array('name'=>'key word', 'id'=>'key word') ); ?>
			
                        <?php echo form_submit( 'submit', 'show tweets' ); ?>
			<?php echo form_close(); ?>
		</div>
		
		<div id="tweets">
			<table width="100%" id="tweets_table">
				<thead>
					<tr>
						<th>text</th>
						<th>image</th>
					</tr>
				</thead>
				<tbody></tbody>
			</table>
		</div>
		
	</div>
	
</div>