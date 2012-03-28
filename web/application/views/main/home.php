<div class="row">
	<div id="tag_list" class="span3"></div>
	<div id="feed_directory" class="span6"></div>
	<div id="feed_reactions" class="span3"></div>
</div>
<script id="feed_item" type="text/template">
	<img src="<%= pic %>" alt="<%= name %>" />
	<h1><%= name %> - <span><%= type %></span></h1>
	<div class="content"><%= content %></div>
	<div class="meta"><%= meta %></div>
</script>