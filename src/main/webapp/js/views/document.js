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
      $.ajax({
        url: '/api/documents/' + id + '/raw',
        headers: { 
          Accept: "text/html,application/xhtml+xml,application/xml;",
          'Accept-Charset': 'ISO-8859-1'
        },
        success: function(data) {
          console.log(data);
          self.$('#documentView').html(data);
          self.$el.show();
        }
      })
      
    }
  })
  
  return DocumentView;
});