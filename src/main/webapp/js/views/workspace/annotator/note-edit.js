define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-edit.html',
        'text!/templates/workspace/annotator/document-note-form.html',
        'text!/templates/workspace/annotator/note-form.html',
        'text!/templates/workspace/annotator/comment.html',
        'ckeditor', 'ckeditor-jquery'],
       function($, _, Backbone, vent, Handlebars, noteEditTemplate,
                documentNoteFormTemplate, noteFormTemplate, commentTemplate,
                CKEditor, ckEditorJquery) {
  Handlebars.registerHelper('when-contains', function(coll, x, options) {
    if (_.contains(coll, x)) {
      return options.fn(this);
    }
  });

  Handlebars.registerHelper('when-eq', function(x, y, options) {
    if (x === y) {
      return options.fn(this);
    }
  });

  var DocumentNoteForm = Backbone.View.extend({
    template: Handlebars.compile(documentNoteFormTemplate),

    events: {'keyup input': 'setDirty'},

    initialize: function() {
      _.bindAll(this, 'render', 'save', 'setDirty', 'annotate', 'extract',
                      'hasPersistedNote', 'close', 'isPersisted');
      var self = this;
      vent.on('document-note:change', function(documentNote) {
                                        if (documentNote) {
                                          self.documentNote = documentNote;
                                          self.isDirty = false;
                                          self.render();
                                        } else {
                                          self.close();
                                        }
                                      });
      this.render();
    },

    render: function() {
      if (!this.documentNote) {
        return;
      }
      this.$el.html(this.template(this.documentNote))
              .effect('highlight', {color: 'lightblue'}, 500);
      if (this.isPersisted()) {
        this.$('#delete-document-note').removeAttr('disabled');
      }
    },

    open: function(documentNote) {
      this.isDirty = false;
      this.documentNote = documentNote;
      this.render();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-document-note').removeAttr('disabled');
    },

    annotate: function(documentId, noteId, selection) {
      if (!this.documentNote) {
        this.documentNote = {note: noteId};
      }
      this.document = documentId;
      this.selection = selection;
      this.documentNote.fullSelection = selection.selection;
      this.render();
      this.setDirty();
    },
    
    close: function() {
      this.documentNote = null;
      this.selection = null;
      this.isDirty = false;
      this.$el.empty();
    },

    extract: function() {
      var arr = this.$el.serializeArray();
      var data = _(arr).reduce(function(acc, field) {
                                 acc[field.name] = field.value;
                                 return acc;
                               }, {});
      if (data.publishable) {
        data.publishable = true;
      } else {
        data.publishable = false;
      }
      data.id = this.documentNote.id;
      if (!data.id) {
        // 'shortenedSelection' is generated in the backend
        if (!data.shortenedSelection) {
          delete data.shortenedSelection;
        }
      }
      data.selection = this.selection;
      data.document = this.document;
      return data;
    },
    
    hasPersistedNote: function() {
      return this.documentNote.note;
    },
    
    save: function(data) {
      var self = this;
      var request = {url: '/api/document-notes/' + data.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(documentNote) {
                       documentNote.document = documentNote.document.id;
                       vent.trigger('document-note:change', documentNote);
                       if (data.selection) {
                         vent.trigger('annotation:change', documentNote.document);
                       }
                     }};
      if (!data.id) {
        request.url = '/api/document-notes/';
        request.type = 'POST';
      }
      $.ajax(request);
    },
    
    isPersisted: function() {
      return this.documentNote.id != null;
    },
    
    remove: function() {
      var self = this;
      var request = {url: '/api/document-notes/' + this.documentNote.id,
                     type: 'DELETE',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     success: function(data) {
                       vent.trigger('annotation:change', self.documentNote.document);
                       // TODO: Note's DocumentNote count?
//                       vent.trigger('note:change', self.note.id);
                       vent.trigger('document-note:change', data);
                     }};
      $.ajax(request);
    }
  });

  var ckEditorSetup = {removePlugins: 'elementspath',
                       height: '40px',
                       skin: 'kama',
                       entities: false,
                       extraPlugins: 'autogrow,onchange',
                       autoGrow_minHeight: '40',
                       resize_enabled: false,
                       startupFocus: false,
                       toolbarCanCollapse: false,
                       toolbar: 'edith',
                       toolbar_edith: [{name: 'basicstyles',
                                        items: ['SpecialChar', 'Bold','Italic',
                                                'Underline', 'Subscript',
                                                'Superscript', '-', 'RemoveFormat']},
                                                {name: 'links', items: ['Link', 'Unlink']},
                                                {name: 'document', items: ['Source']}]};

  var NoteForm = Backbone.View.extend({
    events: {'keyup input': 'setDirty',
             'change input': 'setDirty',
             'change select': 'setDirty'},
  
    template: Handlebars.compile(noteFormTemplate),

    initialize: function() {
      _.bindAll(this, 'render', 'save', 'setDirty', 'open', 'extract');
      var self = this;
      vent.on('note:change', function(note) {
                               self.isDirty = false;
                               self.note = note;
                               self.render();
                             });
      this.render();
    },

    render: function() {
      if (!this.note) {
        return;
      }
      _(CKEditor.instances).each(function(editor) {
        editor.destroy();
      });
      this.$el.html(this.template(this.note))
              .effect('highlight', {color: 'lightblue'}, 500);
      this.$('.wysiwyg').ckeditor(ckEditorSetup);
      var self = this;
      _.each(CKEditor.instances,
             function(editor) {
               editor.on('change', function() { self.setDirty(); });
             });
    },
    
    open: function(note) {
      this.isDirty = false;
      this.note = note;
      this.render();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-note').removeAttr('disabled');
    },
    
    extract: function() {
      var arr = this.$el.serializeArray();
      var data = _(arr).reduce(function(acc, field) {
                                 var name = field.name;
                                 var value = field.value;
                                 var xs = name.split('.');
                                 if (xs.length > 2) {
                                   throw 'Only once nested paths are supported'
                                 } else if (xs.length === 2) {
                                   var o = acc[xs[0]] || {};
                                   o[xs[1]] = value;
                                   acc[xs[0]] = o;
                                 } else {
                                   acc[name] = value;
                                 }
                                 return acc;
                               }, {});
      var types = _(this.$('input[name="types"]').serializeArray())
                    .map(function(field) {
                           return field.value;
                         });
      data.types = types;
      data.id = this.note.id;
      return data;
    },

    save: function(data) {
      var self = this;
      var request = {url: '/api/notes/' + data.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(data) {
                       self.isDirty = false;
                       vent.trigger('note:change', data);
                     }};
      if (!data.id) {
        request.url = '/api/notes/';
        request.type = 'POST';
      }
      $.ajax(request);
    }
  });

  var Comment = Backbone.View.extend({
    template: Handlebars.compile(commentTemplate),

    events: {'click #edit-comment': 'edit'},

    initialize: function() {
      _.bindAll(this, 'render', 'open', 'edit');
      this.render();
    },

    render: function() {
      this.$el.html(this.template(this.comment || {}));
      if (this.comment) {
        this.$('.content').show();
      }
    },

    open: function(comment, noteId) {
      this.comment = comment;
      this.noteId = noteId;
      this.render();
    },
    
    close: function() {
      this.noteId = null;
      this.comment = null;
      this.render();
    },

    edit: function() {
      // TODO: Switch to vent and separate edit view
      var content = prompt('New comment');
      // TODO: Note not persisted?
      var self = this;
      var request = {url: '/api/notes/' + self.noteId + '/comment',
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify({message: content}),
                     success: function(comment) {
                       self.open(comment, self.noteId);
                     }};
      $.ajax(request);
    }
  });

  var NoteEdit = Backbone.View.extend({
    template: Handlebars.compile(noteEditTemplate),

    events: {'click #save-document-note': 'saveDocumentNote',
             'click #save-note': 'saveNote',
             'click #delete-document-note': 'deleteDocumentNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'open', 'create', 'annotate', 
                      'saveDocumentNote', 'saveNote', 'deleteDocumentNote');
      var self = this;
      vent.on('document:selection', function(documentId, selection) {
        if (self.$el.is(':visible')) {
          self.annotate(documentId, self.noteForm.note.id, selection);
        }
      });
      vent.on('note:create', this.create);
      vent.on('document-note:open', this.open);
      this.render();
    },

    render: function() {
      this.$el.html(this.template);
      this.noteForm = new NoteForm({el: this.$('form#note')});
      this.documentNoteForm = new DocumentNoteForm({el: this.$('form#document-note')});
      this.comment = new Comment({el: this.$('div#comment')})
    },

    open: function(documentNote) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      this.documentNoteForm.open(documentNote);
      $.getJSON('/api/notes/' + documentNote.note,
                this.noteForm.open);
      var self = this;
      $.getJSON('/api/notes/' + documentNote.note + '/comment',
                function(comment) {
                  self.comment.open(comment, documentNote.note);
                });
    },

    create: function() {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      this.documentNoteForm.close();
      this.comment.close();
      this.noteForm.open({});
      this.noteForm.setDirty();
    },

    annotate: function(documentId, noteId, selection) {
      // XXX: Do we need dirty checks?
//      if (this.documentNoteForm.isDirty) {
//        if (!confirm('U haz unsaved changes, continue?')) {
//          return;
//        }
//      }
      this.documentNoteForm.annotate(documentId, this.noteForm.note.id, selection);
    },

    saveDocumentNote: function(evt) {
      evt.preventDefault();
      var self = this;
      var documentNote = this.documentNoteForm.extract();
      if (this.documentNoteForm.hasPersistedNote()) {
        this.documentNoteForm.save(documentNote);
      } else {
        // Need to save DocumentNote and Note
        if (!confirm('Note will be saved as well?')) {
          return;
        }
        var data = documentNote;
        data.note = this.noteForm.extract();
        var request = {url: '/api/document-notes/',
                       type: 'POST',
                       dataType: 'json',
                       contentType: "application/json; charset=utf-8",
                       data: JSON.stringify(data),
                       success: function(data) {
                         data.document = data.document.id;
                         vent.trigger('document-note:change', data);
                         vent.trigger('note:change', data.note);
                         vent.trigger('annotation:change', data.document);
                       }};
        $.ajax(request);
      }
    },

    saveNote: function(evt) {
      evt.preventDefault();
      this.noteForm.save(this.noteForm.extract());
    },
    
    deleteDocumentNote: function(evt) {
      evt.preventDefault();
      if (confirm('Delete document note?')) {
        this.documentNoteForm.remove();
      }
    }
  });

  return NoteEdit;
});

