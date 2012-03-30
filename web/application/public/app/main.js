var readreactv = {

	// Create this closure to contain the cached modules
	module: function() {

		// Internal module cache.
		var modules = {};

		// Create a new module reference scaffold or load an existing module.
		return function(name) {

			// If this module has already been created, return it.
			if (modules[name]) {
				return modules[name];
			}

			// Create a module and save it under this name
			return modules[name] = { Views: {} };
		};
	}(),

	fetchTemplate: function(path, done) {
		var JST = window.JST = window.JST || {};

		// Should be an instant synchronous way of getting the template, if it
		// exists in the JST object.
		if (JST[path]) {
			return done(JST[path]);
		}

		// Fetch it asynchronously if not available from JST
		return $.get(path, function(contents) {
			var tmpl = _.template(contents);

			// Set the global JST cache and return the template
			done(JST[path] = tmpl);
		});
	}
};

$(function() {

	// Defining the application router, you can attach sub routers here.
	var Router = Backbone.Router.extend({
		routes: {
			'': 'index'
		},

		index: function() {
			console.log('router - index');
		}
	});

	var feedItemModule = readreactv.module('feeditem');
	var feedItemCollectionView = new feedItemModule.Views.Collection();

	//create router instance
	var router = new Router();

	//start history service
	Backbone.history.start();

});