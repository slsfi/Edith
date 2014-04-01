define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!templates/workspace/document-management/documents.html', 
        'text!templates/workspace/document-management/document-info.html',
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
      vent.on('document:deleted document:renamed', this.reload);
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
          url: 'api/files'
        },

        onActivate: function(node) {
          if (!node.data.isFolder) {
            vent.trigger('document:select', node.data);
          } else {
            self.$('input[name=path]').val(encodeURIComponent(node.data.path));
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
            url: 'api/files/',
            data: 'path=' + encodeURIComponent(node.data.path)
          });
        }
      });
      
    },

    submit: function(evt) {
      evt.preventDefault();
      var formData = new FormData(this.$(".import").get(0));
      var self = this;
      $.ajax('api/documents',
             {type: 'post',
              processData: false,
              contentType: false,
              data: formData,
              success: function() {
                self.reload();
                vent.trigger('documents:reset');
                self.$('input[name=path]').val('');
              }});
    }

  });

  return DocumentsView;
});

