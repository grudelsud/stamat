<div id="content">
	<h1>manage vocabularies</h1>
	<div id="add_feed">
		<?php echo form_open( 'add_tag', array('id' => 'form_add_tag') ); ?>
		<?php echo form_label( 'Tag', 'tag' ); ?>
		<?php echo form_input( array('name'=>'tag', 'id'=>'tag') ); ?>

		<?php echo form_label( 'Parent (select from cloud or leave empty for top level)', 'parent' ); ?>
		<?php echo form_input( array('name'=>'parent', 'id'=>'parent') ); ?>
		<?php echo form_hidden( 'parent_id', '0' ); ?>

		<?php echo form_submit( 'submit', 'add!' ); ?>
		<?php echo form_close(); ?>
	</div>
	<div id="vocabulary_detail">
		<ul id="root">
		</ul>
		<ul id="children">
		</ul>
	</div>
	<div id="vocabularies">
	</div>
</div>