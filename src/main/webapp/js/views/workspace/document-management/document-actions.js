define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!/templates/workspace/document-management/document-actions.html'],
  function($, _, Backbone, vent, localize, Handlebars, template) {
  var template = Handlebars.compile(template);

  var DocumentActionsView = Backbone.View.extend({
    events: {'click #delete': 'delete',
             'click #rename': 'rename'},  

    selectedNode: {},

    initialize: function() {
      _.bindAll(this, 'render', 'select', 'delete', 'rename');
      this.render();

      vent.on('document:select', this.select);
      vent.on('folder:select', this.select);
    },

    render: function(data) {
      this.$el.html(template(data));
    },

    select: function(data) {
      this.selectedNode = data;
      this.$('button').removeAttr('disabled');
    },

    delete: function() {
      var self = this;
      var msg = self.selectedNode.isFolder ? 'remove-folder-confirm' : 'remove-file-confirm';
      if (confirm(localize(msg))) {
        $.ajax('api/files',
                {type: 'delete',
                 data: { path: self.selectedNode.path },
                 success: function() { 
                   vent.trigger('document:delete', this.selectedNode); 
                   self.$('button').attr('disabled', 'disabled');
                 }  
        });
      }
    },

    rename: function() {
      var self = this;
      var newName = prompt(localize('rename'), self.selectedNode.title);
      if (newName) {
        $.ajax('api/files', 
                {type: 'put',
                  data: { path: self.selectedNode.path, name: newName },
                  success: function(data) { 
                    vent.trigger('document:rename', data); 
                    self.$('button').attr('disabled', 'disabled');
                  }
        });
      }
    }
  });

  return DocumentActionsView;
});