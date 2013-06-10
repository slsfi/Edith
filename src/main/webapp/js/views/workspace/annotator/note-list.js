define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'localize', 'spinner',
        'text!/templates/workspace/annotator/note-item.html',
        'views/workspace/annotator/metadata-field-select'],
       function($, _, Backbone, vent, Handlebars, localize, spinner, noteItemTemplate,
                MetadataFieldSelect) {
  // TODO: What happens upon delete/re-render?
  var NoteListItem = Backbone.View.extend({
    tagName: 'li',

    template: Handlebars.compile(noteItemTemplate),

    events: {'click a': 'click',
             'click #edit-note': 'edit',
             'click #comment-note': 'comment'},

    initialize: function() {
      _.bindAll(this, 'render', 'click', 'select', 'edit', 'comment');
      this.documentNote = this.options.data;
      this.render();
      var self = this;
      vent.on('document-note:change', function(documentNote) {
        if (documentNote && documentNote.id === self.documentNote.id) {
          self.documentNote = documentNote;
          self.render();
        }
      });
      vent.on('note:change', function(note) {
        if (note.id === self.documentNote.note.id) {
          self.documentNote.note = note;
          self.render();
        }
      });
      vent.on('comment:change', function(comment, noteId) {
        if (noteId === self.documentNote.note.id) {
          self.documentNote.note.comment = comment;
          self.render();
        }
      });
      vent.on('document-note:select', this.select);
      vent.on('metadata-field-select:change', function(columns) {
        if (self.$el.is(':visible')) {
          self.$('span').hide();
          _.each(columns, function(column) {
            var el = self.$('.' + column);
            if (el.text().length > 0) {
              self.$('.' + column).show();
            }
          });
        }
      });
    },

    render: function() {
      this.$el.html(this.template(this.documentNote));
      if (this.selected) {
        this.$el.css('background', '#EEE');
        this.$('#buttons').show();
      } else {
        this.$el.css('background', 'white');
        this.$('#buttons').hide();
      }
    },

    click: function(evt) {
      evt.preventDefault();
      vent.trigger('document-note:select', this.documentNote.id);
    },

    select: function(id) {
      this.selected = this.documentNote.id === id;
      if (this.selected) {
        this.el.scrollIntoView(true);
      }
      this.render();
    },

    edit: function() {
      vent.trigger('document-note:open', this.documentNote);
    },

    comment: function() {
      var noteId = this.documentNote.note.id;
      spinner('comment:edit');
      $.getJSON('/api/notes/' + noteId + '/comment',
                function(comment) {
                  vent.trigger('comment:edit', noteId, comment);
                });
    }
  });

  var NoteList = Backbone.View.extend({
    events: {'click #create-note': 'createNote'},
    
    initialize: function() {
      _.bindAll(this, 'render', 'createNote');
      // XXX: Ghosts?
      vent.on('document:open annotation:change', this.render);
    },

    render: function(id) {
      new MetadataFieldSelect({el: this.$('.metadata-field-select')});
      this.$('ul.notes').empty();
      spinner('document-notes:loaded');
      var self = this;
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        vent.trigger('document-notes:loaded');
        _(data).each(function(documentNote) {
          self.$('ul.notes').append(new NoteListItem({data: documentNote}).el);
        });
      });
    },
    
    createNote: function() {
      vent.trigger('note:create');
    }
  });

  return NoteList;
});

