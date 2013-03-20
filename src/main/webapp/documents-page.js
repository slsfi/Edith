require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'js/views/documents',
           'js/views/document', 'js/views/comments', 'text!templates/header.html'],
          function($, _, Backbone, DocumentsView, DocumentView, CommentsView, headerTemplate) {
    $('#header').append(headerTemplate);
    new DocumentsView({el: $('#documents')});
    new CommentsView({el: $('#comments')});
    new DocumentView({el: $('#document')});
  });
});
