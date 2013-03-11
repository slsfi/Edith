define(['jquery', 'underscore', 'backbone', 'js/vent', 'handlebars'],
  function($, _, Backbone, vent, Handlebars) {
  var DocumentView = Backbone.View.extend({
    initialize: function() {
      vent.on('document:open', this.render);
    },
    
    render: function(id) {
      console.log('somebody wants to open document: ' + id);
    }
  })
  
  return DocumentView;
});