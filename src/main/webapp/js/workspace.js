require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'vent', 'localize', 'moment',
           'views/workspace/annotator', 'views/workspace/document-management',
           'header'],
          function($, _, Backbone, Handlebars, vent, localize, moment, Annotator, DocumentManagement, Header) {
    new Header({el: $('#header')});


    Handlebars.registerHelper('dateFormat', function(timestamp, block) {
      var f = block.hash.format || "D.M.YYYY";
      return moment.unix(timestamp/1000).format(f);
    });

    var WorkspaceRouter = Backbone.Router.extend({
      views: {},

      initialize: function() {
        _.bindAll(this);
        this.views = {documentManagement: new DocumentManagement({el: $('#document-management')}),
                      annotator: new Annotator({el: $('#annotator')})};
        var self = this;
        vent.on('route:change', function(path) {
          self.navigate(path, {trigger: true})
        });
      },

      routes: {'': 'openDocumentManagement',
              'documents/:id': 'openAnnotator'},

      hideAll: function() {
        _(this.views).each(function(view) {
          view.$el.hide();
        });
      },

      openDocumentManagement: function() {
        this.hideAll();
        this.views.documentManagement.$el.show();
      },

      openAnnotator: function(id) {
        this.hideAll();
        vent.trigger('document:open', parseInt(id));
        this.views.annotator.$el.show();
      }
    });

    var router = new WorkspaceRouter();
    Backbone.history.start();

    // To enable persistent document listing view when navigating
    // on documents page. TODO: Think of a better way to do this.
    $('#workspace').click(function(ev) {
      ev.preventDefault();
      router.navigate('', {trigger: true});
    });


  });

});

