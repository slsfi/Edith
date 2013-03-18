define(['jquery', 'underscore', 'backbone', 'js/vent', 'handlebars'],
  function($, _, Backbone, vent, Handlebars) {
  var DocumentView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      vent.on('document:open', this.render);
    },
    
    render: function(id) {
      var self = this;
      // FIXME: We know better
      $.get('/api/documents/'+id+'/raw', function(data) {
        self.$('#documentView').html(data);
        self.$el.show();
      });
      
    }
  })
  
  return DocumentView;
});