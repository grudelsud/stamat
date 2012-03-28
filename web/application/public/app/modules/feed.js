(function(Feed) {

	// dependency
	var Tag = readreactv.module('tag');

	var FeedModel = Backbone.Model.extend({
		initialize: function() {
			this.set({ tags: new TagList() });
		}
	});

	var FeedList = Backbone.Collection.extend({
		model: FeedModel
	});

	var FeedView = Backbone.View.extend({});

})(readreactv.module('feed'));