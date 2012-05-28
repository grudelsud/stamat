(function(Medium) {

	Medium.Model = Backbone.Model.extend({});

	Medium.Collection = Backbone.Collection.extend({
		model: Medium.Model
	});

	Medium.Views.Main = Backbone.View.extend({});

})(readreactv.module('medium'));