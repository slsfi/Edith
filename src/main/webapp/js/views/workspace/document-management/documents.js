define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/documents.html', 'dynatree'],
  function($, _, Backbone, vent, localize, Handlebars, template) {

  var template = Handlebars.compile(template);
  
  var DocumentsView = Backbone.View.extend({
    events: {'submit .import': 'submit'},

    initialize: function() {
      _.bindAll(this);
      this.render();
    },

    render: function() {
      this.$el.html(template());
      var self = this;
      this.$('#directoryTree').dynatree({
        initAjax: {
          url: '/api/files'
        },

        onClick: function(node) {
          if (!node.data.isFolder) {
            vent.trigger('document:select', node.data.documentId);
          } else {
            vent.trigger('folder:select');
          }
        },

        onDblClick: function(node) {
          if (!node.data.isFolder) {
            vent.trigger('route:change', 'documents/' + node.data.documentId);
          }
        },

        onActivate: function(node) {
          if (node.data.isFolder) {
            // TODO: Open + delete links?
          } else {
            // TODO: Open + delete links?
          }
        },

        onLazyRead: function(node) {
          node.appendAjax({
            url: '/api/files/',
            data: 'path=' + node.data.path
          });
        }

      });
    },

    submit: function() {
      var self = this;
      var formData = new FormData(this.$(".import").get(0));
      $.ajax('api/documents',
             {type: 'post',
              processData: false,
              contentType: false,
              data: formData,
              success: function(data) {
                // TODO use proper event for this
                self.$('#directoryTree').dynatree("getTree").reload();
              }});
      return false;
    }

  });

  return DocumentsView;
});