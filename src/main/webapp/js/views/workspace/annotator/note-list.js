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
    },

    render: function() {
      this.attributes = {'data-id': this.documentNote.id};
      this.$el.html(this.template(this.documentNote)).attr('data-id', this.documentNote.id);
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
                'selectNote', 'clickNote', 'showFieldsAccordingToSelection');
      vent.on('document:open annotation:change document-note:deleted', this.render);
      vent.on('document:selection-change', this.toggleCreateButton);
      vent.on('document-note:select', this.selectNote);
      var self = this;
      vent.on('metadata-field-select:change', function(columns) {
        if (self.$el.is(':visible')) {
          self.showFieldsAccordingToSelection(columns);
        }
      });
    },

    showFieldsAccordingToSelection: function(columns) {
      this.$('li span').removeClass('selected-metadata').addClass('unselected-metadata');
      var self = this;
      _.each(columns, function(column) {
        var el = self.$('li .' + column);
        if (el.text().length > 0) {
          self.$('li .' + column).removeClass('unselected-metadata').addClass('selected-metadata');
        }
      });
    },

    render: function(id) {
      if (!this.noteListMetadata) {
        this.noteListMetadata = new MetadataFieldSelect({el: this.$('#note-list-metadata'),
                                                         defaultSelection: ['description']});
      }

      this.$('ul.notes').empty();
      spinner('document-notes:loaded');
      var self = this;
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        var start = new Date().getTime();
        var fragment = document.createDocumentFragment();
        _(data).each(function(documentNote) {
          fragment.appendChild(new NoteListItem({metadataSelect: self.noteListMetadata,
                                                 data: documentNote}).el);
        });
        self.$('ul.notes').append(fragment);
        self.$('.note-buttons').hide();
        self.showFieldsAccordingToSelection(self.noteListMetadata.getColumns());
        vent.trigger('document-notes:loaded');
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
      var $el = this.$('li[data-id="' + id + '"]');
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

