define(['jquery', 'underscore', 'backbone', 'vent', 'localize',
        'handlebars', 'text!templates/workspace/document-management/comments.html'],
  function($, _, Backbone, vent, localize, Handlebars, template) {
  var template = Handlebars.compile(template);

  var CommentsView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      var render = this.render;
      vent.on('document:select', function(node) {
        $.get('api/documents/' + node.documentId + '/note-comments', render);
      });
      var self = this;
      vent.on('folder:select', function() { self.$el.empty(); });
    },

    render: function(data) {
      this.$el.html(template(data));
    }
  })

  return CommentsView;
});
