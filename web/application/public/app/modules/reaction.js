(function(Reaction) {

	// dependency
	var Tag = readreactv.module('tag');
	var Media = readreactv.module('media');
	var Tweet = readreactv.module('tweet');

	Reaction.Model = Backbone.Model.extend({
		defaults: {
			tags: new Tag.Collection(),
			media: new Media.Collection(),
			tweets: new Tweet.Collection()
		},
		urlRoot: base_url + 'index.php/json/reactions/id',
		parse: function(response) {
			var result = response.success;
			var content = result.content;
			this.set({
				tags: new Tag.Collection(result.tags),
				media: new Media.Collection(result.media)
			});
			return content;
		}
	});

	Reaction.Views.Main = Backbone.View.extend({
		template: assets_url+'app/templates/reaction.html',
		templateTweet: assets_url+'app/templates/tweet.html',
		el: '#reaction_directory',
		events: {
			'click .label': 'tagSelect',
			'click .read-text': 'openPermalink'
		},
		initialize: function() {
			this.model.on('change', this.render, this);
			this.loadingTweets = false;
		},
		empty: function() {
			var view = this;
			view.$el.empty();
		},
		render: function() {
			var view = this;
			// Fetch the template, render it to the View element and call done.
			if(typeof this.model.get('permalink') !== 'undefined') {
				readreactv.fetchTemplate(this.template, function(tmpl) {
					view.$el.html(tmpl(view.model.toJSON()));
				});
			}
			return this;
		},
		loadTweets: function() {
			this.loadingTweets = true;
			var selected_tags = this.model.get('tags').where({selected: true});
			var query_terms = [];
			_.each(selected_tags, function(tag) { query_terms.push(encodeURI(tag.get('name'))); });
			var tweet_collection = this.model.get('tweets');
			var tweet_template;
			readreactv.fetchTemplate(this.templateTweet, function(tmpl) {tweet_template = tmpl;});
			tweet_collection.query = query_terms.join('+');
			tweet_collection.fetch({
				success: function(tweets) {
					var $tweet_list = $('#content_tweets').empty();
					_.each(tweets.models, function(tweet) {
						$tweet_list.append(tweet_template(tweet.toJSON()));
					});
					this.loadingTweets = false;
				}
			});
		},
		tagSelect: function(e) {
			var $label = $(e.target);
			var tag_id = $label.attr('data-id');
			var tag_obj = this.model.get('tags').get(tag_id);

			$label.toggleClass('label-success');
			if($label.hasClass('label-success')) {
				tag_obj.set({selected: true});
			} else {
				tag_obj.set({selected: false});
			}
			this.loadTweets();
		},
		openPermalink: function(e) {
			e.preventDefault();
			var url = base_url + 'index.php/admin/permalink/' + this.model.id;
			console.log(url);
			$('#permalink_content').empty().append('<iframe src="'+url+'"></iframe>');
			$('#permalink_container').modal('show');

		}
	});

})(readreactv.module('reaction'));