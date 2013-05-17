require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'localize', 'text!/templates/header.html'],
          function($, _, Backbone, Handlebars, localize, headerTemplate) {
    var headerTemplate = Handlebars.compile(headerTemplate);
    $('body').prepend(headerTemplate());    
  });
});
