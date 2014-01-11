define(['jquery', 'underscore', 'backbone', 'vent',
        'text!templates/workspace/document-management.html',
        'views/workspace/document-management/document-actions',
        'views/workspace/document-management/documents',
        'views/workspace/document-management/comments'],
  function($, _, Backbone, vent, template, DocumentActionsView, DocumentsView, CommentsView) {
  var DocumentManagement = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },

    render: function() {
      this.$el.html(template);
      new DocumentsView({el: this.$('#documents')});
      new DocumentActionsView({el: this.$('#document-actions')});
      new CommentsView({el: this.$('#comments')});
    }
  });

  return DocumentManagement;
});
