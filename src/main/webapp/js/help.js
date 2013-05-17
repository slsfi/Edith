require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'localize', 'text!/templates/header.html'],
          function($, _, Backbone, localize, headerTemplate) {
    $('body').prepend(headerTemplate);    
  });
});
