<div class="tabbable">
	<div class="row">
		<div class="span12">
			<ul class="nav nav-tabs">
				<li class="active"><a href="#tab_feed" data-toggle="tab">Feed</a></li>
				<li><a href="#tab_map" data-toggle="tab">Map</a></li>
				<li><a href="#tab_media" data-toggle="tab">Media</a></li>
			</ul>
		</div>
	</div>
</div>
<div class="tab-content">
	<!-- #tab_feed -->
	<div class="tab-pane active" id="tab_feed">
		<div class="row">
			<div class="span2">
				<a class="btn btn-danger" href="#">Import</a>
			</div>
			<div class="span6">
				<div class="pagination"></div>
			</div>
			<div class="span4"></div>
		</div>
		<div class="row" id="tab_feed_content">
			<div id="feed_container" class="span2">
				<h3>Select Feed</h3>
				<div id="tag_directory"></div>		
			</div>
			<div id="feeditem_container" class="span6">
				<h3>Read Articles</h3>
				<div id="feed_directory"></div>
				<div class="pagination"></div>
			</div>
			<div id="reaction_container" class="span4 scroll-top">
				<h3>Read Reactions</h3>
				<div id="reaction_directory"></div>		
			</div>
		</div>
	</div><!-- #tab_feed -->

	<!-- #tab_map -->
	<div class="tab-pane" id="tab_map">
		<div class="row">
			<div id="map_container" class="span12">
			</div>
		</div>
	</div><!-- #tab_map -->

		<!-- #tab_media -->
	<div class="tab-pane" id="tab_media">
		<div class="row">
			<div class="span12">
				<p>howdy, media</p>
			</div>
		</div>
	</div><!-- #tab_media -->

</div>
<?php $this->load->view('modal_permalink'); ?>