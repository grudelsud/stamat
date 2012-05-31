(function(Reaction) {

	// dependency
	var Tag = readreactv.module('tag');
	var Media = readreactv.module('media');
	var Tweet = readreactv.module('tweet');

	Reaction.Model = Backbone.Model.extend({
		defaults: {
			tags: new Tag.Collection(),
			media: new Media.Collection(),
			tweets: new Tweet.Collection(),
			content: {}
		},
		urlRoot: base_url + 'index.php/json/reactions/id',
		parse: function(response) {
			var result = response.success;
			if( typeof result !== undefined ) {
				var content = result.content;
				content.tags = new Tag.Collection(result.tags);
				content.media = new Media.Collection(result.media);
				return content;
			}
		}
	});

	Reaction.Views.Main = Backbone.View.extend({
		template: assets_url+'app/templates/reaction.html',
		events: {
			'click .label': 'tagSelect'
		},
		initialize: function() {
			this.setElement( $('#reaction_directory') );
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
		tagSelect: function(e) {
			// pretty awful, this should be made at model-level instead of view-level
			// means we should change the selected value of the underlying model instance of the label
			var $label = $(e.target).parent();
			$label.toggleClass('label-success');
		}
	});

})(readreactv.module('reaction'));