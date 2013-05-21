define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-edit.html'],
       function($, _, Backbone, vent, Handlebars, template) {
  var NoteEdit = Backbone.View.extend({
    template: Handlebars.compile(template),

    initialize: function() {
      _.bindAll(this, 'render');
      var self = this;
      vent.on('note:open', function(id) {
                             $.getJSON('/api/document-notes/' + id,
                                       {note: true},
                                       self.render);
                           });
    },

    render: function(note) {
      this.$el.html(this.template(note));
    }
  });

  return NoteEdit;
});

