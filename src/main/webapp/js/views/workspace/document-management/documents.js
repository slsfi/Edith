define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/documents.html', 
        'text!/templates/workspace/document-management/document-info.html',
        'dynatree'],
  function($, _, Backbone, vent, localize, Handlebars, template, docInfoTemplate, dynatree) {

  var template = Handlebars.compile(template);
  var docInfoTemplate = Handlebars.compile(docInfoTemplate);

  var DocumentItemView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this);
      this.render();
    },

    render: function() {
      this.$el.append(docInfoTemplate(this.model));
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
        
        onRender: function(node, span) {
          if (!node.data.isFolder) {
            new DocumentItemView({el: $(span), model: node.data});
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

