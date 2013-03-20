require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'js/vent', 'js/views/documents',
           'js/views/document', 'js/views/comments', 'text!templates/header.html'],
          function($, _, Backbone, vent, DocumentsView, DocumentView, CommentsView, headerTemplate) {
    $('#header').append(headerTemplate);
    var documents = new DocumentsView({el: $('#documents')});
    var comments = new CommentsView({el: $('#comments')});
    var document = new DocumentView({el: $('#document')});
    vent.on('document:open', function() {
      documents.$el.hide();
      comments.$el.hide();
      document.$el.show();
    });
  });
});
