(function(FeedItem) {

	FeedItem.Model = Backbone.Model.extend({
		defaults: {
			pic: 'http://placehold.it/160x120'
			// pic: assets_url+'img/app/feed_item_placeholder.png'
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
			var result = response.success;
			this.meta = result.meta;
			return result.items;
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
			var scroll = (e.pageY - 160) + 'px';
			$('.scroll-top').animate({'margin-top': scroll}, 'fast');
		}
	});

	FeedItem.Views.Collection = Backbone.View.extend({
		el: '#feed_directory',
		initialize: function() {
			this.collection.on('reset', this.render, this);
			this.collection.on('change', this.render, this);
		},
		render: function() {
			this.$el.empty();
			var view = this;
			_.each(this.collection.models, function(feed_item) {
				var feed_item_view = new FeedItem.Views.Main({model: feed_item});
				view.$el.append(feed_item_view.render().el);
			}, this);
			this.addPagination();
		},
		addPagination: function() {
			var $pagination = $('.pagination');
			var $list = $('<ul></ul>');
			if( this.collection.meta.page > 1 ) {
				$list.append('<li><a href="#!/feeds/'+this.collection.meta.prev+'">&larr; Previous</a></li>');
			}
			$list.append('<li class="disabled"><a href="#">'+this.collection.meta.page+'/'+this.collection.meta.count_all_pages+'</a></li>');
			if( this.collection.meta.page < this.collection.meta.count_all_pages ) {
				$list.append('<li><a href="#!/feeds/'+this.collection.meta.next+'">Next &rarr;</a></li>');
			}
			$pagination.empty().append($list);
		}
	});

})(readreactv.module('feeditem'));