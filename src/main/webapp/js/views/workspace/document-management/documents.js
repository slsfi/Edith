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
      this.$el.append(actionsTemplate(this.model.data));
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
      var newName = prompt(localize('rename'), this.model.data.title);
      if (newName) {
        $.ajax('api/files', 
            {type: 'put',
             data: { path: this.model.data.path, name: newName },
             success: function(data) { vent.trigger('document:rename', data); }
        });
      }
    }
  });
  
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
        
        onRender: function(node, span) {
          new DocumentItemView({el: $(span), model: node});
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