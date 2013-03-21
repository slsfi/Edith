define(['jquery', 'underscore', 'backbone', 'js/vent',
        'handlebars', 'text!templates/comments.html'],
  function($, _, Backbone, vent, Handlebars, template) {
  var template = Handlebars.compile(template);
  
  var CommentsView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      var render = this.render;
      vent.on('document:select', function(id) {
        $.get('/api/documents/' + id + '/note-comments', render);
      });
    },
    
    render: function(data) {
      this.$el.html(template(data));
    }
  })
  
  return CommentsView;
});