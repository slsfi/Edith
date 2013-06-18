define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'localize', 'spinner',
        'text!/templates/workspace/annotator/note-edit.html',
        'text!/templates/workspace/annotator/document-note-form.html',
        'text!/templates/workspace/annotator/note-form.html',
        'text!/templates/workspace/annotator/comment.html',
        'ckeditor', 'ckeditor-jquery', 'ckeditor-setup'],
       function($, _, Backbone, vent, Handlebars, localize, spinner, noteEditTemplate,
                documentNoteFormTemplate, noteFormTemplate, commentTemplate,
                CKEditor, ckEditorJquery, ckEditorSetup) {
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
    // used when updating the annotation
    currentSelection: {},

    template: Handlebars.compile(documentNoteFormTemplate),

    events: {'keyup input': 'setDirty',
             'click #update-full-selection': 'annotate'},

    initialize: function() {
      _.bindAll(this, 'render', 'save', 'setDirty', 'linkToExistingNote', 'toggleAnnotationEnabled', 'annotate', 'extract',
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
      this.selection = null;
      this.documentNote = documentNote;
      this.render();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-document-note').removeAttr('disabled');
    },

    linkToExistingNote: function(note) {
      if (!this.documentNote) {
        this.documentNote = {note: note};
      } else {
        this.documentNote.note = note;
      }
    },

    toggleAnnotationEnabled: function(documentId, noteId, selection) {
      if (selection && selection.selection.length > 0) {
        this.$('#update-full-selection').removeAttr('disabled');
        this.currentSelection = {'documentId': documentId, 'noteId': noteId, 'selection': selection};
      } else {
        this.$('#update-full-selection').attr('disabled', 'disabled');
        this.currentSelection = {};
      }
    },

    annotate: function() {      
      if (!this.documentNote) {
        this.documentNote = {note: this.currentSelection.noteId};
      }
      this.document = this.currentSelection.documentId;
      this.selection = this.currentSelection.selection;
      this.documentNote.fullSelection = this.currentSelection.selection.selection;
      this.render();
      this.setDirty();
      this.$('#update-full-selection').attr('disabled', 'disabled');
      this.currentSelection = {};
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
      spinner('document-note:change');
      var request = {url: '/api/document-notes/' + data.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(documentNote) {
                       documentNote.document = documentNote.document.id;
                       vent.trigger('document-note:change', documentNote);
                       if (data.selection) {
                         self.selection = null;
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
      spinner('annotation:change', 'document-note:change');
      var self = this;
      var request = {url: '/api/document-notes/' + this.documentNote.id,
                     type: 'DELETE',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     success: function(data) {
                       vent.trigger('annotation:change', self.documentNote.document.id);
                       // TODO: Note's DocumentNote count?
//                       vent.trigger('note:change', self.note.id);
                       vent.trigger('document-note:change', data);
                     }};
      $.ajax(request);
    }
  });


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
      this.$('#type-select').multiselect({});
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

      var types = _(this.$('select[name="type"]').serializeArray())
                    .map(function(field) {
                           return field.value;
                         });
      data.types = types;
      data.id = this.note.id;
      return data;
    },

    save: function(data) {
      spinner('note:change');
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
      _(CKEditor.instances).each(function(editor) {
        editor.destroy();
      });
      $.ajax(request);
    }
  });

  var Comment = Backbone.View.extend({
    template: Handlebars.compile(commentTemplate),

    events: {'click #edit-comment': 'edit'},

    initialize: function() {
      _.bindAll(this, 'render', 'open', 'edit');
      var self = this;
      vent.on('comment:change', function(comment, noteId) {
                                  if (self.$el.is(':visible')) {
                                    self.open(comment, noteId);
                                  }
                                });
      this.render();
    },

    render: function() {
      this.$el.html(this.template(this.comment || {}));
      if (this.comment) {
        this.$('.content').show();
      }
    },

    open: function(comment, noteId) {
      this.comment = comment;
      this.noteId = noteId;
      this.render();
      this.$el.effect('highlight', {color: 'lightblue'}, 500);
    },
    
    close: function() {
      this.noteId = null;
      this.comment = null;
      this.render();
    },

    edit: function() {
      vent.trigger('comment:edit', this.noteId, this.comment);
    }
  });

  var NoteEdit = Backbone.View.extend({
    template: Handlebars.compile(noteEditTemplate),

    events: {'click #save-document-note': 'saveDocumentNote',
             'click #save-note': 'saveNote',
             'click #delete-document-note': 'deleteDocumentNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'openNote', 'openDocumentNote', 'create',
                      'saveDocumentNote', 'saveNote', 'deleteDocumentNote', 'createDocumentNote', 'linkExistingDocumentNote');
      var self = this;
      vent.on('document:selection-change', function(documentId, selection) {
          if (self.$el.is(':visible')) {
          self.documentNoteForm.toggleAnnotationEnabled(documentId, self.noteForm.note ? self.noteForm.note.id : null, selection);
        }
      });
      vent.on('note:create', this.create);
      vent.on('document-note:open', this.openDocumentNote);
      vent.on('note:open', this.openNote);
      vent.on('document-note:create', this.createDocumentNote);
      vent.on('note:link-existing', this.linkExistingDocumentNote);
      this.render();
    },

    render: function() {
      this.$el.html(this.template);
      this.noteForm = new NoteForm({el: this.$('form#note')});
      this.documentNoteForm = new DocumentNoteForm({el: this.$('form#document-note')});
      this.comment = new Comment({el: this.$('div#comment')})
    },

    openNote: function(note) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm(localize('dirty-dialog-confirm'))) {
          return;
        }
      }
      this.documentNoteForm.close();
      this.noteForm.open(note);
      this.comment.open(note.comment, note.id);
    },

    openDocumentNote: function(documentNote) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm(localize('dirty-dialog-confirm'))) {
          return;
        }
      }
      this.documentNoteForm.open(documentNote);
      this.noteForm.open(documentNote.note);
      this.comment.open(documentNote.note.comment, documentNote.note.id);
    },

    create: function() {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm(localize('dirty-dialog-confirm'))) {
          return;
        }
      }
      this.documentNoteForm.close();
      this.comment.close();
      this.noteForm.open({});
      this.noteForm.setDirty();
      vent.trigger('note:new');
    },

    createDocumentNote: function(documentId, selection) {
      this.documentNoteForm.toggleAnnotationEnabled(documentId, null, selection);
      this.documentNoteForm.annotate();
    },

    saveDocumentNote: function(evt) {
      evt.preventDefault();
      var self = this;
      var documentNote = this.documentNoteForm.extract();
      if (this.documentNoteForm.hasPersistedNote()) {
        documentNote.note = this.noteForm.note.id;
        this.documentNoteForm.save(documentNote);
      } else {
        // Need to save DocumentNote and Note
        if (!confirm(localize('save-note-confirm'))) {
          return;
        }
        var data = documentNote;
        data.note = this.noteForm.extract();
        spinner('document-note:change', 'note:change', 'annotation:change');
        var request = {url: '/api/document-notes/',
                       type: 'POST',
                       dataType: 'json',
                       contentType: "application/json; charset=utf-8",
                       data: JSON.stringify(data),
                       success: function(data) {
                         self.documentNoteForm.selection = null;
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
      if (confirm(localize('confirm-document-note-delete'))) {
        this.documentNoteForm.remove();
      }
    },

    linkExistingDocumentNote: function(documentNote) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm(localize('dirty-dialog-confirm'))) {
          return;
        }
      }
      
      this.documentNoteForm.close();
      this.documentNoteForm.linkToExistingNote(documentNote);
      this.comment.close();
      this.noteForm.open(documentNote);
      vent.trigger('note:new');
    }
  });

  return NoteEdit;
});

