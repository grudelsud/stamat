(function(Feed) {

	// dependency
	var Tag = readreactv.module('tag');

	Feed.Model = Backbone.Model.extend({
		defaults: {
			tags: new Tag.Collection()
		},
		parse: function(response) {
			response.tags = new Tag.Collection(response.tags)
			return response;
		}
	});

	Feed.Collection = Backbone.Collection.extend({
		model: Feed.Model,
		url: base_url+'index.php/json/feeds',
		parse: function(response) {
			return response.success;
		}
	});

	Feed.Views.Collection = Backbone.View.extend({
		initialize: function() {
			_.bindAll(this, 'render');
			this.collection.bind('reset', this.render);
			this.collection.bind('change', this.render);
		},
		render: function() {
			this.$el = $('#tag_directory').empty().append('<ul></ul>');
			var view = this;
			_.each(this.collection.models, function(feed) {
				_.each(feed.get('tags').models, function(tag) {
					if(!view.$el.has('li.'+tag.get('slug')).length) {
						view.$el.find(':first').append('<li class="'+tag.get('slug')+'"><a href="/#!/feeds/tag/'+tag.get('slug')+'">'+tag.get('name')+'</a> <ul></ul></li>');
					}
					view.$el.find('li.'+tag.get('slug')+' ul').append('<li><a href="/#!/feeds/id/'+feed.get('id')+'">'+feed.get('title')+'</a></li>');
				}, view);
			}, this);
		}

	});

})(readreactv.module('feed'));