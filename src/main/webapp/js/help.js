require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'localize', 'header'],
          function($, _, Backbone, Handlebars, localize, Header) {
    new Header({el: $('#header')});
  });
});
