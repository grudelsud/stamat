(function(FeedItem) {

	var test_items = [
		{ name: 'test', type: 'fashion', content: 'bla bla bla...', meta: 'posted 25.12.12'},
		{ name: 'test', type: 'fashion', content: 'bla bla bla...', meta: 'posted 26.12.12'},
		{ name: 'test', type: 'fashion', content: 'bla bla bla...', meta: 'posted 27.12.12'},
		{ name: 'test', type: 'fashion', content: 'bla bla bla...', meta: 'posted 28.12.12'}
	];

	FeedItem.Model = Backbone.Model.extend({
		defaults: {
			pic: assets_url+'img/app/feed_item_placeholder.png'
		}
	});

	FeedItem.Collection = Backbone.Collection.extend({
		model: FeedItem.Model
	});

	FeedItem.Views.Main = Backbone.View.extend({
		tagName: 'article',
		className: 'feed_item_container',
		template: assets_url+'app/templates/feeditem.html',
		render: function() {
			var view = this;

			// Fetch the template, render it to the View element and call done.
			readreactv.fetchTemplate(this.template, function(tmpl) {
				view.$el.html(tmpl(view.model.toJSON()));
			});
			return this;
		}
	});

	FeedItem.Views.Collection = Backbone.View.extend({
		initialize: function() {
			// todo: grab data from server to initialize the collection
			this.collection = new FeedItem.Collection( test_items );
			this.render();
		},
		render: function() {
			this.$el = $('#feed_directory');
			var view = this;
			_.each(this.collection.models, function(feed_item) {
				var feed_item_view = new FeedItem.Views.Main({model: feed_item});
				view.$el.append(feed_item_view.render().el);
			}, this);
		}
	});

})(readreactv.module('feeditem'));