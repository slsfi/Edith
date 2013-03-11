define(['jquery', 'underscore', 'backbone', 'js/views/documents'],
  function($, _, Backbone, DocumentsView) {
  var initialize = function() {
    new DocumentsView({el: $('#documents')});
  }
  
  return { initialize: initialize };
});