define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'localize', 'spinner',
        'text!templates/workspace/annotator/note-edit.html',
        'text!templates/workspace/annotator/document-note-form.html',
        'text!templates/workspace/annotator/document-note-form-stub.html',
        'text!templates/workspace/annotator/note-form.html',
        'text!templates/workspace/annotator/comment.html',
        'ckeditor', 'ckeditor-jquery', 'ckeditor-setup'],
       function($, _, Backbone, vent, Handlebars, localize, spinner, noteEditTemplate,
                documentNoteFormTemplate, documentNoteFormStubTemplate, noteFormTemplate,
                commentTemplate, CKEditor, ckEditorJquery, ckEditorSetup) {
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

  Handlebars.registerHelper('when-not-eq', function(x, y, options) {
    if (x !== y) {
      return options.fn(this);
    }
  });


  var DocumentNoteForm = Backbone.View.extend({
    // used when updating the annotation
    currentSelection: {},

    template: Handlebars.compile(documentNoteFormTemplate),
    stubTemplate: Handlebars.compile(documentNoteFormStubTemplate),

    events: {'keyup input': 'setDirty',
    'keyup textarea': 'setDirty',
             'click #update-full-selection': 'annotate',
             'click #create-document-note': 'annotate'},

    initialize: function() {
      _.bindAll(this, 'render', 'save', 'setDirty', 'attach', 'toggleAnnotationEnabled', 'annotate', 'extract',
                      'hasPersistedNote', 'close', 'isPersisted');
      var self = this;
      vent.on('document-note:change', function(documentNote) {
                                        self.documentNote = documentNote;
                                        self.isDirty = false;
                                        self.render();
                                      });
      vent.on('note:change', function(note) {
        if (self.documentNote) {
          self.documentNote.note = note;
        } else {
          self.documentNote = {note: note};
        }
      });
      vent.on('document-note:deleted', this.close);
      this.render();
    },

    render: function() {
      if (!this.documentNote) {
        this.$el.html(this.stubTemplate);
      } else {
        this.$el.html(this.template(this.documentNote))
                .effect('highlight', {color: 'lightblue'}, 500);
        if (this.isPersisted()) {
          this.$('#delete-document-note').removeAttr('disabled');
        }
      }
    },

    open: function(documentNote) {
      this.isDirty = false;
      this.selection = null;
      this.documentNote = documentNote;
      this.render();
    },

    setDirty: function() {
//      if (this.$('input[name=shortenedSelection]').val() === '') {
        if (this.$('#shortenedSelection').val() === '') {
        this.$('#shortenedSelection').parent().parent().addClass('error');
        this.$('#save-document-note').attr('disabled', 'disabled');
      } else {
        this.$('#shortenedSelection').parent().parent().removeClass('error');
        this.$('#save-document-note').removeAttr('disabled');
      }
      this.isDirty = true;
    },

    attach: function(note) {
      if (!this.documentNote) {
        this.documentNote = {note: note};
      } else {
        this.documentNote.note = note;
      }
    },

    toggleAnnotationEnabled: function(documentId, noteId, selection) {
      if (selection && selection.selection.length > 0) {
        this.$('#update-full-selection').removeAttr('disabled');
        this.$('#create-document-note').removeAttr('disabled');
        this.currentSelection = {documentId: documentId, noteId: noteId, selection: selection};
      } else {
        this.$('#create-document-note').attr('disabled', 'disabled');
        this.$('#update-full-selection').attr('disabled', 'disabled');
        this.currentSelection = {};
      }
    },

    annotate: function(evt) { 
      if (evt) { evt.preventDefault(); }
      if (!this.documentNote) {
        this.documentNote = {note: this.currentSelection.noteId};
      }
      this.document = this.currentSelection.documentId;
      this.selection = this.currentSelection.selection;
      this.documentNote.fullSelection = this.currentSelection.selection.selection;
      this.documentNote.shortenedSelection = this.currentSelection.selection.selection;
      this.render();
      this.setDirty();
      this.$('#update-full-selection').attr('disabled', 'disabled');
      this.currentSelection = {};
      vent.trigger('selection:updated', this.documentNote.shortenedSelection);
    },
    
    close: function() {
      this.documentNote = null;
      this.selection = null;
      this.isDirty = false;
      this.render();
    },

    extract: function() {
      var arr = this.$('form').serializeArray();
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
      var request = {url: 'api/document-notes/' + data.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(documentNote) {
                       vent.trigger('document-note:change', documentNote);
                       if (data.selection) {
                         self.selection = null;
                         vent.trigger('annotation:change', documentNote.document.id);
                       }
                     }};
      if (!data.id) {
        request.url = 'api/document-notes/';
        request.type = 'POST';
      }
      $.ajax(request);
    },
    
    isPersisted: function() {
      return this.documentNote.id != null;
    },
    
    remove: function() {
      spinner('document-note:deleted');
      var self = this;
      var request = {url: 'api/document-notes/' + this.documentNote.id,
                     type: 'DELETE',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     success: function(data) {
                       vent.trigger('document-note:deleted', self.documentNote.document.id);
                     }};
      $.ajax(request);
    }
  });


  var NoteForm = Backbone.View.extend({
    events: {'keyup input': 'setDirty',
    'keyup textarea': 'setDirty',
             'change input': 'setDirty',
                 'change textarea': 'setDirty',
             'change select': 'setDirty'},
  
    template: Handlebars.compile(noteFormTemplate),

    initialize: function() {
      _.bindAll(this, 'render', 'save', 'setDirty', 'open', 'extract', 'remove', 'close');
      var self = this;
      vent.on('note:change', function(note) {
                               self.isDirty = false;
                               self.note = note;
                               self.render();
                             });
      vent.on('document-note:deleted', function() {
                                         var request = {url: 'api/notes/' + self.note.id,
                                                        type: 'GET',
                                                        dataType: 'json',
                                                        contentType: "application/json; charset=utf-8",
                                                        success: function(data) {
                                                                   // TODO: Perhaps publish this?
                                                                   self.isDirty = false;
                                                                   self.note = data;
                                                                   self.render();
                                                                 }};
                                         $.ajax(request);
                                       });
      vent.on('note:deleted', this.close);
      this.render();
    },

    render: function() {
      if (!this.note) {
        return;
      }

      _(CKEditor.instances).each(function(editor) {
        editor.destroy(true);
      });
      this.note.allEditors = _(this.note.allEditors)
                               .map(function(user) { 
                                      return user.username;
                                    })
                               .join(', ');
      this.$el.html(this.template(this.note))
              .effect('highlight', {color: 'lightblue'}, 500);
      
      var locked = this.note.status == 'FINISHED'; 
      if (locked) {
        var setup = _.extend(ckEditorSetup);
        setup.readOnly = true;
        this.$('.wysiwyg').ckeditor(setup);               
        this.$(':input[name!="status"]').prop('disabled', true);
      } else {
        this.$('.wysiwyg').ckeditor(ckEditorSetup);
      }
      CKEditor.instances['description'].setReadOnly(locked);
      CKEditor.instances['sources'].setReadOnly(locked);

      var self = this;
      _.each(CKEditor.instances,
             function(editor) {
               // XXX chrome / contentEditable fix
               if (!locked && editor.document) {
                 editor.document.$.body.setAttribute("contenteditable", true);  
               }
               editor.on('change', function() { self.setDirty(); });
             });

      this.$('#type-select').multiselect({
        buttonText: function(options, select) {
          if (options.length == 0) {
            return localize('nothing-selected');
          } else if (options.length > 4) {
            return options.length + ' ' + localize('n-selected') + ' <b class="caret"></b>';
          } else {
            var selected = '';
            options.each(function() {
              selected += $(this).text() + ', ';
            });
            return selected.substr(0, selected.length -2) + ' <b class="caret"></b>';
          }
        }
      });
    },
    
    open: function(note) {
      this.isDirty = false;
      this.note = note;
      this.render();
    },

    close: function() {
      this.isDirty = false;
      this.note = null;
      this.$el.empty();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-note').removeAttr('disabled');
    },
    
    extract: function() {
      var arr = this.$el.children().serializeArray();
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

      if (data.term) {
        if (data.term.language === '') {
          data.term.language = null;
        }
        if (data.term.basicForm === '') {
          data.term.basicForm = null;
        }
      }

      var types = _(this.$('select[name="types"]').serializeArray())
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
      var request = {url: 'api/notes/' + data.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(data) {
                       self.isDirty = false;
                       vent.trigger('note:change', data);
                     }};
      if (!data.id) {
        request.url = 'api/notes/';
        request.type = 'POST';
      }
      _(CKEditor.instances).each(function(editor) {
        editor.destroy(true);
      });
      $.ajax(request);
    },

    remove: function() {
      spinner('note:deleted');
      var self = this;
      var request = {url: 'api/notes/' + this.note.id,
                     type: 'DELETE',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     success: function(data) {
                       vent.trigger('note:deleted');
                     }};
      $.ajax(request);
}
  });

  var Comment = Backbone.View.extend({
    template: Handlebars.compile(commentTemplate),

    events: {'click #edit-comment': 'edit'},

    initialize: function() {
      _.bindAll(this, 'render', 'open', 'edit', 'close');
      var self = this;
      vent.on('comment:change', function(comment, noteId) {
                                  if (self.$el.is(':visible')) {
                                    self.open(comment, noteId);
                                  }
                                });
      vent.on('note:change', function(note) {
                               if (self.$el.is(':visible')) {
                                 if (note.id) {
                                   self.open(note.comment, note.id);
                                 } else {
                                   self.close();
                                 }
                               }
                             });
      vent.on('note:deleted', this.close);
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
      this.$el.empty();
    },

    edit: function() {
      vent.trigger('comment:edit', this.noteId, this.comment);
    }
  });

  var NoteEdit = Backbone.View.extend({
    template: Handlebars.compile(noteEditTemplate),

    events: {'click #save-document-note': 'saveDocumentNote',
             'click #save-note': 'saveNote',
             'click #delete-document-note': 'deleteDocumentNote',
             'click #delete-note': 'deleteNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'openNote', 'openDocumentNote', 'create',
                      'saveDocumentNote', 'saveNote', 'deleteDocumentNote', 'createDocumentNote', 'attach');
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
      vent.on('note:link-existing', this.attach);
      vent.on('document:open', function() {
        self.noteForm.close();
        self.comment.close();
        self.documentNoteForm.close();
      });
      this.render();
    },

    render: function() {
      this.$el.html(this.template);
      this.noteForm = new NoteForm({el: this.$('div#note')});
      this.documentNoteForm = new DocumentNoteForm({el: this.$('div#document-note')});
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
      this.noteForm.open({term: {language: 'SWEDISH'}});
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
        // #59 - save both document note and note without confirm

        var data = documentNote;
        data.note = this.noteForm.extract();
        spinner('document-note:change', 'note:change', 'annotation:change');
        var request = {url: 'api/document-notes/',
                       type: 'POST',
                       dataType: 'json',
                       contentType: "application/json; charset=utf-8",
                       data: JSON.stringify(data),
                       success: function(data) {
                         self.documentNoteForm.selection = null;
                         vent.trigger('document-note:change', data);
                         vent.trigger('note:change', data.note);
                         vent.trigger('annotation:change', data.document.id);
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
        if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
          if (!confirm(localize('dirty-dialog-confirm'))) {
            return;
          }
        }
        this.documentNoteForm.remove();
      }
    },

    deleteNote: function(evt) {
      evt.preventDefault();
      if (confirm(localize('confirm-note-delete'))) {
        if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
          if (!confirm(localize('dirty-dialog-confirm'))) {
            return;
          }
        }
        this.noteForm.remove();
      }
    },

    attach: function(note) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm(localize('dirty-dialog-confirm'))) {
          return;
        }
      }
      this.documentNoteForm.close();
      this.documentNoteForm.attach(note);
      this.comment.close();
      this.comment.open(note.comment, note.id);
      this.noteForm.open(note);
      vent.trigger('note:new');
    }
  });

  return NoteEdit;
});

