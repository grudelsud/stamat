(function(Tweet) {

	Tweet.Model = Backbone.Model.extend({});

	Tweet.Collection = Backbone.Collection.extend({
		model: Tweet.Model
	});

	Tweet.Views.Main = Backbone.View.extend({});

})(readreactv.module('tweet'));