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
	}()
};

$(function() {

	// Defining the application router, you can attach sub routers here.
	var Router = Backbone.Router.extend({
		routes: {
			'': 'index'
		},

		index: function() {
		}
	});

	var itemDirectory = new readreactv.module('feeditem');

	//create router instance
	var mainRouter = new Router();

	//start history service
	Backbone.history.start();

});