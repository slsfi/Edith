define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-item.html'],
       function($, _, Backbone, vent, Handlebars, noteItemTemplate) {

  var noteItemTemplate = Handlebars.compile(noteItemTemplate);
  var NoteList = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      vent.on('document:open annotation:created', this.render);
    },

    render: function(id) {
      this.$el.empty();
      var self = this;
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        _(data).each(function(note) {
          self.$el.append(noteItemTemplate(note));
        });
      });
    }
  });

  return NoteList;
});