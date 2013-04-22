require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'js/vent',
           'js/views/documents-page/document', 'js/views/documents-page/listing',
           'text!templates/header.html'],
          function($, _, Backbone, vent, DocumentView, ListingView, headerTemplate) {
    $('#header').append(headerTemplate);
    
    var DocumentsRouter = Backbone.Router.extend({
      views: {},
      
      initialize: function() {
        _.bindAll(this);
        this.views = {listing: new ListingView({el: $('#listing')}),
                      document: new DocumentView({el: $('#document')})};
        var self = this;
        vent.on('route:change', function(path) {
          self.navigate(path, {trigger: true})
        });
      },
      
      routes: {'': 'listing',
              'documents/:id': 'showDocument'},

      hideAll: function() {
        _(this.views).each(function(view) {
          view.$el.hide();
        }); 
      },
              
      listing: function() {
        this.hideAll();
        this.views.listing.$el.show();
      },
      
      showDocument: function(id) {          
        this.hideAll();
        vent.trigger('document:open', id);
        this.views.document.$el.show();
      }
    });
    
    var router = new DocumentsRouter();
    Backbone.history.start();
  });
});
