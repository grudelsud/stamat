<div id="content">
	<h1>manage vocabularies</h1>
	<div id="add_tag">
		<?php echo form_open( 'add_tag', array('id' => 'form_add_tag') ); ?>
		<?php echo form_label( 'Tag', 'tag' ); ?>
		<?php echo form_input( array('name'=>'tag', 'id'=>'tag') ); ?>

		<?php echo form_label( 'Parent (select from cloud, leave empty for top level or <a id="clear" href="#">[click to clear]</a>)', 'parent' ); ?>
		<?php echo form_input( array('name'=>'parent', 'id'=>'parent', 'disabled'=>'disabled') ); ?>
		<?php echo form_hidden( 'parent_id', '0' ); ?>

		<?php echo form_submit( 'submit', 'add!' ); ?>
		<?php echo form_close(); ?>
	</div>
	<div id="vocabulary_detail">
		<h1>root tags</h1>
		<ul id="root">
		</ul>
		<h1>children tags</h1>
		<ul id="children">
		</ul>
		<hr/>
		<h1>colour codes</h1>
		<ul>
			<li>node</li>
			<li class="selected">selected</li>
			<li class="parent_over">parent</li>
			<li class="child_over">child</li>
		</ul>
	</div>
</div>