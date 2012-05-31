(function(FeedItem) {

	FeedItem.Model = Backbone.Model.extend({
		defaults: {
			pic: assets_url+'img/app/feed_item_placeholder.png'
		}
	});

	FeedItem.Collection = Backbone.Collection.extend({
		model: FeedItem.Model,
		setFilter: function(params) {
			this.params = params || '';
		},
		url: function() {
			var params = this.params || '';
			return base_url + 'index.php/json/feeditems/' + params;
		},
		parse: function(response) {
			return response.success;
		}
	});

	FeedItem.Views.Main = Backbone.View.extend({
		tagName: 'article',
		className: 'feed_item_container',
		template: assets_url+'app/templates/feeditem.html',
		events: {
			'click h3': 'itemSelect'
		},
		render: function() {
			var view = this;
			// Fetch the template, render it to the View element and call done.
			readreactv.fetchTemplate(this.template, function(tmpl) {
				view.$el.html(tmpl(view.model.toJSON()));
			});
			return this;
		},
		itemSelect: function(e) {
			var scroll = (e.pageY - 77) + 'px';
			$('.scroll-top').animate({'padding-top': scroll}, 'fast');
		}
	});

	FeedItem.Views.Collection = Backbone.View.extend({
		initialize: function() {
			this.collection.on('reset', this.render, this);
			this.collection.on('change', this.render, this);
		},
		render: function() {
			this.$el = $('#feed_directory').empty();
			var view = this;
			_.each(this.collection.models, function(feed_item) {
				var feed_item_view = new FeedItem.Views.Main({model: feed_item});
				view.$el.append(feed_item_view.render().el);
			}, this);
		},
	});

})(readreactv.module('feeditem'));