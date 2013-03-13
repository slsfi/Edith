require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'js/views/documents',
           'js/views/document', 'text!templates/header.html'],
          function($, _, Backbone, DocumentsView, DocumentView, headerTemplate) {
    $('#header').append(headerTemplate);
    new DocumentsView({el: $('#documents')});
    new DocumentView({el: $('#document')});
  });
});
