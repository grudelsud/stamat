<div class="row">
	<div id="feed_container" class="span12">
		<div id="vocabulary_detail" class="row">
			<div class="span4">
			<h3>topics <button type="button" id="slide_topics" class="slide">toggle</button></h3>
			<div id="topics"></div>				
			</div>

			<div class="span4">
			<h3>entities <button type="button" id="slide_entities" class="slide">toggle</button></h3>
			<div id="entities"></div>
			</div>

			<div class="span4">
			<h3>teamlife entities <button type="button" id="slide_teamlife_sanr" class="slide">toggle</button></h3>
			<div id="teamlife_sanr"></div>
			</div>
		</div>

		<div id="tagged_items">
			<div id="feed_pagination" class="pagination">
			</div>
			<div id="feed_content">
			</div>
		</div>	
	</div>
</div>

<?php $this->load->view('admin/modal_permalink'); ?>