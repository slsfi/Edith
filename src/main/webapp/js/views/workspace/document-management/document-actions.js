define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/document-actions.html'],
  function($, _, Backbone, vent, localize, Handlebars, template) {
  var template = Handlebars.compile(template);
  var selectedNode;

  var DocumentActionsView = Backbone.View.extend({
    events: {'click #delete': 'delete',
             'click #rename': 'rename'},  

    initialize: function() {
      _.bindAll(this, 'render');
      this.render();

      vent.on('document:select', this.select);
      vent.on('folder:select', this.select);
    },

    render: function(data) {
      this.$el.html(template(data));
    },

    select: function(data) {
      selectedNode = data;
      $('#document-actions button').each(function() {
        $(this).removeAttr('disabled');
      });
    },

    delete: function() {
      var self = this;
      var msg = selectedNode.isFolder ? 'remove-folder-confirm' : 'remove-file-confirm';
      if (confirm(localize(msg))) {
        $.ajax('api/files',
                {type: 'delete',
                 data: { path: selectedNode.path },
                 success: function() { 
                   vent.trigger('document:delete', selectedNode); 
                   $('#document-actions button').each(function() {
                     $(this).attr('disabled', 'disabled');
                   });
                 }  
        });
      }
    },

    rename: function() {
      var self = this;
      var newName = prompt(localize('rename'), selectedNode.title);
      if (newName) {
        $.ajax('api/files', 
                {type: 'put',
                  data: { path: selectedNode.path, name: newName },
                  success: function(data) { 
                    vent.trigger('document:rename', data); 
                    $('#document-actions button').each(function() {
                      $(this).attr('disabled', 'disabled');
                    });
                  }
        });
      }
    }
  });

  return DocumentActionsView;
});