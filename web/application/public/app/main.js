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
	},
	models: {},
	views: {},
	collections: {},
	routers: {}
};

$(function() {

	// Defining the application router, you can attach sub routers here.
	var Router = Backbone.Router.extend({
		routes: {
			'!/tags' : 'tags',
			'!/feeds/*params' : 'feeds',
			'!/feeditems/*params' : 'feeditems',
			'!/reactions/*params' : 'reactions',
			'': 'index'
		},
		initialize: function() {
			var feedModule = readreactv.module('feed');
			readreactv.collections.feedCollection = new feedModule.Collection();
			readreactv.views.feedCollectionView = new feedModule.Views.Collection({collection: readreactv.collections.feedCollection});

			var feedItemModule = readreactv.module('feeditem');
			readreactv.collections.feedItemCollection = new feedItemModule.Collection();
			readreactv.views.feedItemCollectionView = new feedItemModule.Views.Collection({collection: readreactv.collections.feedItemCollection});
		},
		tags: function() {
			console.log('router - tags');
		},
		feeds: function( params ) {
			// console.log('router - feeds ' + params);
			readreactv.collections.feedItemCollection.setFilter(params);
			readreactv.collections.feedItemCollection.fetch();
		},
		feeditems: function( params ) {
			console.log('router - feeditems ' + params);
		},
		reactions: function( params ) {
			console.log('router - reactions ' + params);
		},
		index: function() {
			// console.log('hey');
			readreactv.collections.feedCollection.fetch();
			readreactv.collections.feedItemCollection.setFilter();
			readreactv.collections.feedItemCollection.fetch();
		}
	});

	//create router instance
	readreactv.routers.appRouter = new Router();
	//start history service
	Backbone.history.start();
});