$(function() {

	var feed_item_list = [
		{ pic: 'test.png', name: 'blog post', type: 'post', content: 'bla bla bla...', meta: 'posted on 2015-12-25' },
		{ pic: 'test.png', name: 'blog post', type: 'post', content: 'bla bla bla...', meta: 'posted on 2015-12-25' },
		{ pic: 'test.png', name: 'blog post', type: 'post', content: 'bla bla bla...', meta: 'posted on 2015-12-25' },
		{ pic: 'test.png', name: 'blog post', type: 'post', content: 'bla bla bla...', meta: 'posted on 2015-12-25' },
		{ pic: 'test.png', name: 'blog post', type: 'post', content: 'bla bla bla...', meta: 'posted on 2015-12-25' }
	];
	/**
	 * Models
	 */
	var FeedItem = Backbone.Model.extend({
		defaults: {
			pic: assets_url+'img/app/feed_item_placeholder.png'
		}
	});

	/**
	 * Collections
	 */
	var FeedItemList = Backbone.Collection.extend({
		model: FeedItem
	});

	/**
	 * Views
	 */
	var FeedItemView = Backbone.View.extend({
		tagName: 'article',
		className: 'feed_item_container',
		template: _.template( $('#feed_item').html() ),
		render: function() {
        	this.$el.html(this.template(this.model.toJSON()));
        	return this;
		}
	});

	var DirectoryView = Backbone.View.extend({
		el: $('#feed_directory'),
		initialize: function() {
			// todo: grab data from server to initialize the collection
			this.collection = new FeedItemList(feed_item_list);
			this.render();
		},
		render: function() {
			var that = this;
			_.each(this.collection.models, function(feed_item) {
				var feed_item_view = new FeedItemView({model: feed_item});
				that.$el.append(feed_item_view.render().el);
			}, this);
		}
	});

	var directory = new DirectoryView();
});