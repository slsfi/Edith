define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'localize', 'spinner', 'raw-selection',
        'text!/templates/workspace/annotator/note-item.html',
        'views/workspace/annotator/metadata-field-select'],
       function($, _, Backbone, vent, Handlebars, localize, spinner, rawSelection, noteItemTemplate,
                MetadataFieldSelect) {
  // TODO: What happens upon delete/re-render?
  var NoteListItem = Backbone.View.extend({
    tagName: 'li',

    events: {'click #edit-note': 'edit',
             'click #comment-note': 'comment'},

    template: Handlebars.compile(noteItemTemplate),

    initialize: function() {
      _.bindAll(this, 'render', 'edit', 'comment');
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
      this.$el.attr('data-id', this.documentNote.id);
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
    },
  });

  var NoteList = Backbone.View.extend({
    events: {'click #create-note': 'createNote',
             'click #select-note': 'clickNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'createNote', 'toggleCreateButton',
                'selectNote', 'clickNote');
      vent.on('document:open annotation:change', this.render);
      vent.on('document:selection-change', this.toggleCreateButton);
      vent.on('document-note:select', this.selectNote);
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
        console.log(self.$el);
        self.$('.note-buttons').hide();
      });
    },

    toggleCreateButton: function(documentId, selection) {
      if (selection && selection.selection.length > 0) {
        this.$('#create-note').removeAttr('disabled');
      } else {
        this.$('#create-note').attr('disabled', 'disabled');
      }
    },

    selectNote: function(id) {
      this.$('li').removeClass('selected');
      this.$('.note-buttons').hide();
      var $el = this.$('[data-id="' + id + '"]');
      $el.addClass('selected');
      $el.find('.note-buttons').show();
      $el.get(0).scrollIntoView(true);

    },

    clickNote: function(evt) {
      evt.preventDefault();
      var id = $(evt.target).attr('data-id');
      vent.trigger('document-note:select', id);
    },

    createNote: function() {
      var selection = rawSelection();
      if (selection.toString() === '') {
        alert(localize('no-text-selected'));
      } else {
        vent.trigger('note:create');
      }
    }
  });

  return NoteList;
});

