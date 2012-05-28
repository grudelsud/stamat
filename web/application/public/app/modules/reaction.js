(function(Reaction) {

	// dependency
	var Tag = readreactv.module('tag');
	var Medium = readreactv.module('medium');
	var Tweet = readreactv.module('tweet');

	Reaction.Collections = {};

	Reaction.Model = Backbone.Model.extend({
		defaults: {
			tags: new Tag.Collection(),
			media: new Medium.Collection(),
			tweets: new Tweet.Collection(),
			content: {}
		}
		url: base_url + 'index.php/json/reactions/id',
		parse: function(response) {
			var result = response.success;
			response.tags = new Tag.Collection(response.tags);
			response.media = new Medium.Collection(response.media);
			return result.content;
		}
	});

	Reaction.Views.Main = Backbone.View.extend({
	});

})(readreactv.module('reaction'));