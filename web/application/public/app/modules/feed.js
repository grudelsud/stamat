(function(Feed) {

	// dependency
	var Tag = readreactv.module('tag');

	Feed.Model = Backbone.Model.extend({
		initialize: function() {
			this.set({ tags: new Tag.Collection() });
		}
	});

	Feed.Collection = Backbone.Collection.extend({
		model: Feed.Model
	});

	Feed.Views.Main = Backbone.View.extend({});

})(readreactv.module('feed'));