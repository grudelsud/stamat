(function(FeedItem) {

	 var FeedItemModel = Backbone.Model.extend({
	 	defaults: {
	 		pic: assets_url+'img/app/feed_item_placeholder.png'
	 	}
	 });

	 var FeedItemList = Backbone.Collection.extend({
	 	model: FeedItemModel
	 });

	 var FeedItemView = Backbone.View.extend({
	 	tagName: 'article',
	 	className: 'feed_item_container',
	 	template: _.template( $('#feed_item').html() ),
	 	render: function() {
	 		this.$el.html(this.template(this.model.toJSON()));
	 		return this;
	 	}
	 });

	 var FeedItemListView = Backbone.View.extend({
	 	el: $('#feed_directory'),
	 	initialize: function() {
			// todo: grab data from server to initialize the collection
			this.collection = new FeedItemList();
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

})(readreactv.module('feeditem'));