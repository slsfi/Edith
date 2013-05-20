define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/documents.html', 
        'text!/templates/workspace/document-management/actions.html', 'dynatree'],
  function($, _, Backbone, vent, localize, Handlebars, template, actionsTemplate) {

  var template = Handlebars.compile(template);
  
  var actionsTemplate = Handlebars.compile(actionsTemplate);
  
  var DocumentItemView = Backbone.View.extend({
    events: {'click .delete': 'deleteItem',
             'click .rename': 'renameItem'},
    
    initialize: function() {
      _.bindAll(this);
      this.render();
    },
    
    render: function() {
      this.$el.append(actionsTemplate());
    },
    
    deleteItem: function() {
      var self = this;
      var msg = this.model.data.isFolder ? 'remove-folder-confirm' : 'remove-file-confirm';
      if (confirm(localize(msg))) {
        $.ajax('api/files',
            {type: 'delete',
             data: { path: this.model.data.path },
             success: function() { vent.trigger('document:delete', self.model.data); } 
        });
      }
    },
    
    renameItem: function() {
      console.log("rename");
    }
  });
  
  var DocumentsView = Backbone.View.extend({
    events: {'submit .import': 'submit'},

    initialize: function() {
      _.bindAll(this);
      var self = this;
      vent.on('document:delete', function(data) {
        self.$('#directoryTree').dynatree("getTree").reload();
      });
      
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
        
        onCreate: function(node, span) {
          new DocumentItemView({el: $(span), model: node});
        },

        onLazyRead: function(node) {
          node.appendAjax({
            url: '/api/files/',
            data: 'path=' + node.data.path
          });
        }

      });
            
      $("#directoryTree").on("mouseenter", ".dynatree-node", function() {
        var node = $.ui.dynatree.getNode(this);
        $(this).find(".actions").show();
      });      
      $("#directoryTree").on("mouseleave", ".dynatree-node", function() {
        var node = $.ui.dynatree.getNode(this);
        $(this).find(".actions").hide();
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