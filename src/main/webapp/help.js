require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'text!templates/header.html'],
          function($, _, Backbone, headerTemplate) {
    $('body').prepend(headerTemplate);
  });   
});
