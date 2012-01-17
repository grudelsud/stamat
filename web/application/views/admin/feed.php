<div id="content">
	<h1>manage feeds</h1>
	<div id="add_feed">
		<?php echo form_open( 'add_feed', array('id' => 'form_add_feed') ); ?>
		<?php echo form_label( 'Title', 'title' ); ?>
		<?php echo form_input( array('name'=>'title', 'id'=>'title') ); ?>

		<?php echo form_label( 'URL', 'url' ); ?>
		<?php echo form_input( array('name'=>'url', 'id'=>'url') ); ?>

		<?php echo form_submit( 'submit', 'add!' ); ?>
		<?php echo form_close(); ?>
	</div>
	<div id="feed_detail">
		<div id="feed_controls"></div>
		<div id="feed_content"></div>
	</div>
	<div id="feeds">
		<table id="feeds_table">
			<thead>
				<tr>
					<th>id</th>
					<th>title</th>
					<th>url</th>
				</tr>
			</thead>
			<tbody></tbody>
		</table>
	</div>
</div>