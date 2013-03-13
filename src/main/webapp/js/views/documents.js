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
        
        onDblClick: function(node, event) {
          if (!node.data.isFolder) {
            vent.trigger('document:open', node.data.documentId);
            // FIXME: Don't do this Vesa
            self.$el.hide();
          }
        },
        
        onKeypress: function(node, event) {
          var code = event.keyCode || event.which;
          if (code === 46) {
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