(function(Mediafinder) {

	// dependency
	var Media = readreactv.module('media');

	Mediafinder.Model = Backbone.Model.extend({
		defaults: {
			media: new Media.Collection(),
			params: ''
		},
		urlRoot: function() {
			var url = base_url + 'index.php/json/media/' + this.get('params');
			return url;
		},
		parse: function(response) {
			var result = response.success;
			this.set({
				media: new Media.Collection(result.media)
			});
			return null;
		}
	});

	Mediafinder.Views.Main = Backbone.View.extend({
		template: assets_url+'app/templates/mediafinder.html',
		el: '#media_directory',
		events: {
			'click .item': 'itemSelect'
		},
		initialize: function() {
			this.model.on('change', this.render, this);
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
			e.preventDefault();
			var $label = $(e.target);
			var tag_id = $label.attr('data-id');
			console.log('click ' + tag_id);
		}
	});

})(readreactv.module('mediafinder'));