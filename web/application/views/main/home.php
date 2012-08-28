<div class="row">
	<!-- sidebar navigation -->
	<div class="span2">
		<div id="tag_directory"></div>
	</div>

	<!-- content -->
	<div class="span10">
		<!-- tabs -->
		<div class="tabbable">
			<ul class="nav nav-tabs">
				<li class="active"><a href="#tab_feed" data-toggle="tab">Read</a></li>
				<li><a href="#tab_media" data-toggle="tab">View</a></li>
				<li><a href="#tab_map" id="tab_map_link" data-toggle="tab">Visit</a></li>
			</ul>
		</div>

		<!-- tab content -->
		<div class="tab-content">

			<!-- #tab_feed -->
			<div class="tab-pane active" id="tab_feed">
				<div class="row">
					<div id="feed_item_container" class="span6">
						<div class="pagination"></div>
						<div id="feed_directory"></div>
						<div class="pagination"></div>
					</div>
					<div id="reaction_container" class="span4 scroll-top">
						<div id="reaction_directory"></div>
					</div>
				</div>
			</div><!-- #tab_feed -->

			<!-- #tab_media -->
			<div class="tab-pane" id="tab_media">
				<div class="row">
					<div id="media_container" class="span6">
						<div id="media_directory"></div>
					</div>
					<div id="similarity_container" class="span4 scroll-top">
						<div id="similarity_directory"></div>
					</div>
				</div>
			</div><!-- #tab_media -->

			<!-- #tab_map -->
			<div class="tab-pane" id="tab_map">
				<div id="map_canvas"></div>
			</div><!-- #tab_map -->
		</div>
	</div>
</div>

<?php $this->load->view('modal_permalink'); ?>