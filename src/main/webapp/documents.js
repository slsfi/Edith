require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'js/views/documents', 'text!templates/header.html'],
          function($, _, Backbone, DocumentsView, headerTemplate) {
    $('body').prepend(headerTemplate);
    new DocumentsView({el: $('#documents')});
  });   
});
