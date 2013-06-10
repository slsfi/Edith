define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/comment-edit.html'],
       function($, _, Backbone, vent, Handlebars, commentEditTemplate) {
  var CommentEdit = Backbone.View.extend({
    template: Handlebars.compile(commentEditTemplate),

    events: {'click #save-comment': 'save'},

    initialize: function() {
      _.bindAll(this, 'render', 'save');
      var self = this;
      vent.on('comment:edit', function(noteId, comment) {
        self.noteId = noteId;
        self.comment = comment || {};
        self.render();
      });
    },

    render: function() {
      this.$el.modal({show: true});
      this.$el.html(this.template(this.comment));
    },

    save: function() {
      var message = this.$('textarea').val();
      spinner('comment:change');
      // TODO: What if note is not already persisted?
      var self = this;
      var request = {url: '/api/notes/' + self.noteId + '/comment',
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify({message: message}),
                     success: function(comment) {
                       vent.trigger('comment:change', comment, self.noteId);
                     }};
      $.ajax(request);
    }
  });

  return CommentEdit;
});
