require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'vent', 'localize', 'moment',
           'views/workspace/annotator', 'views/workspace/document-management',
           'header', 'bootstrap-notify'],
          function($, _, Backbone, Handlebars, vent, localize, moment, Annotator, DocumentManagement, Header, BootstrapNotify) {

    $(document).ajaxError(function(event, jqxhr, settings, exception) {
                            vent.trigger('ajax:error', jqxhr);
                          });

    var Notifications = Backbone.View.extend({
      initialize: function() {
        var self = this;
        var success = function(message) {
          self.$el.notify({message: {text: message}}).show() 
        }
        var error = function(message) {
          self.$el.notify({message: {text: message}, fadeOut: {enabled: false}, type: 'error'}).show() 
        }
        vent.on('ajax:error', function(jqxhr) {
          error(localize('error-message') + ': HTTP ' + jqxhr.status + ', ' + jqxhr.statusText);
        });
        _.each({'document-note:change': localize('instance-saved-message'),
                'annotation:change': localize('annotation-saved-message'),
                'document-note:deleted': localize('instance-deleted-message'),
                'note:deleted': localize('note-deleted-message'),
                'note:change': localize('note-saved-message'),
                'comment:change': localize('comment-saved-message'),
                'documents:reset': localize('documents-uploaded-message'),
                'document:deleted': localize('document-deleted-message'),
                'document:renamed': localize('document-renamed-message')},
               function(msg, evt) {
                 vent.on(evt, function() { success(msg) });
               });
      }
    });
    
    new Notifications({el: $('#notifications')});

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

