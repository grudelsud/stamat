(function(Tag) {

	var TagModel = Backbone.Model.extend({});

	var TagList = Backbone.Collection.extend({
		model: TagModel
	});

	var TagView = Backbone.View.extend({});

})(readreactv.module('tag'));