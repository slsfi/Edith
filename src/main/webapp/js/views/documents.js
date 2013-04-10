define(['jquery', 'underscore', 'backbone', 'js/vent',
        'handlebars', 'text!templates/documents.html', 'dynatree'],
  function($, _, Backbone, vent, Handlebars, template) {
  var DocumentsView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },
    
    render: function() {
      this.$('#documentListing').html(template);
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
            vent.trigger('document:open', node.data.documentId);
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
    }
  })
  
  return DocumentsView;
});