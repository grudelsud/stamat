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

			var req_data = {};
			req_data.index = 'all';
			req_data.source = 'index';
			req_data.fileidentifier = $label.attr('data-id');
			req_data.feature = $('#select_vs_descriptor').val();
			req_data.numberofresults = 30;

			console.log(req_data);
/*
			$.ajax({
				type: 'post',
				contentType: 'application/json',
				url: 'http://fom.londondroids.com:9000/visualSimilarity',
				data: req_data,
				success: function(result) {
					$('#similarity_directory').empty().append(JSON.stringify(result));
				},
				error: function(jqXHR, textStatus, errorThrown) {
					$('#similarity_container').before('<div class="alert alert-error"><button class="close" data-dismiss="alert">×</button>'+ textStatus +' '+ jqXHR.status +' - '+ errorThrown +'</div>');
				}

			});
*/
		}
	});

})(readreactv.module('mediafinder'));