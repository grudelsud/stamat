$(function() {

	/**
	 * Models
	 */
	var Feed = Backbone.Model.extend({
	});

	var FeedItem = Backbone.Model.extend({
		defaults: {
			pic: assets_url+'img/app/feed_item_placeholder.png'
		}
	});

	/**
	 * Views
	 */
	var FeedItemView = Backbone.View.extend({
		tagName: 'article',
		className: 'feed_item_container',
		template: $('#feed_item').html(),
		render: function() {
			var tmpl = _.template(this.template);
        	this.$el.html(tmpl(this.model.toJSON()));
        	return this;
		}
	});

	/**
	 * Collections
	 */
	var FeedList = Backbone.Collection.extend({
		model: Feed
	});
});