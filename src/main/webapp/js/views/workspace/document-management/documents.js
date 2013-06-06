define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/documents.html', 
        'dynatree'],
  function($, _, Backbone, vent, localize, Handlebars, template) {

  var template = Handlebars.compile(template);
  
  var DocumentsView = Backbone.View.extend({
    events: {'submit .import': 'submit'},

    initialize: function() {
      _.bindAll(this);
      vent.on('document:delete document:rename', this.reload);
      this.render();
    },
    
    reload: function() {
      this.$('#directory-tree').dynatree('getTree').reload();
    },

    render: function() {
      this.$el.html(template());
      var self = this;
      this.$('#directory-tree').dynatree({
        initAjax: {
          url: '/api/files'
        },

        onClick: function(node) {
          if (!node.data.isFolder) {
            vent.trigger('document:select', node.data);
          } else {
            vent.trigger('folder:select', node.data);
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
            
      this.$("#directory-tree").on("mouseenter", ".dynatree-node", function() {
        $(this).find(".actions").show();
      });      
      
      this.$("#directory-tree").on("mouseleave", ".dynatree-node", function() {
        $(this).find(".actions").hide();
      });
      
    },

    submit: function() {
      var formData = new FormData(this.$(".import").get(0));
      $.ajax('api/documents',
             {type: 'post',
              processData: false,
              contentType: false,
              data: formData,
              success: this.reload});
      return false;
    }

  });

  return DocumentsView;
});