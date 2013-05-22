define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-item.html'],
       function($, _, Backbone, vent, Handlebars, noteItemTemplate) {
  // TODO: What happens upon delete/re-render?
  var NoteListItem = Backbone.View.extend({
    tagName: 'li',

    template: Handlebars.compile(noteItemTemplate),

    events: {'click': 'open'},

    initialize: function() {
      _.bindAll(this, 'render', 'open');
      this.documentNote = this.options.data;
      this.render();
      var self = this;
      vent.on('document-note:change', function(documentNote) {
        if (documentNote.id === self.documentNote.id) {
          self.documentNote = documentNote;
          self.render();
        }
      })
    },

    render: function() {
      this.$el.html(this.template(this.documentNote));
    },

    open: function(evt) {
      evt.preventDefault(); 
      vent.trigger('document-note:open', this.documentNote);
    }
  });

  var NoteList = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      vent.on('document:open annotation:created', this.render);
    },

    render: function(id) {
      this.$el.empty();
      var self = this;
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        _(data).each(function(documentNote) {
          self.$el.append(new NoteListItem({data: documentNote}).el);
        });
      });
    }
  });

  return NoteList;
});

