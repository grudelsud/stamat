<div id="content">
	<div id="vocabulary_select">
		<h1>select vocabulary</h1>
		<select></select>
	</div>
	<h1>manage vocabularies</h1>
	<div id="add_tag">
		<?php echo form_open( 'add_tag', array('id' => 'form_add_tag') ); ?>
		<?php echo form_label( 'Tag', 'tag' ); ?>
		<?php echo form_input( array('name'=>'tag', 'id'=>'tag') ); ?>

		<?php echo form_label( 'Parent (select from cloud, leave empty for top level or <a id="clear" href="#">[click to clear]</a>)', 'parent' ); ?>
		<?php echo form_input( array('name'=>'parent', 'id'=>'parent', 'disabled'=>'disabled') ); ?>
		<?php echo form_hidden( 'parent_id', '0' ); ?>
		<?php echo form_hidden( 'vocabulary_id', '0' ); ?>

		<?php echo form_submit( 'submit', 'add!' ); ?>
		<?php echo form_close(); ?>
	</div>
	<div id="vocabulary_detail">
		<div id="tags"></div>
		<div id="tag_controls"><button type="button" class="delete">delete selected tags</button></div>
		<hr/>
		<h1>colour codes</h1>
		<ul>
			<li><span>node</span></li>
			<li><span class="selected">selected</span></li>
			<li><span class="parent_over">parent</span></li>
			<li><span class="child_over">child</span></li>
		</ul>
	</div>
</div>