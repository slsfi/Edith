define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-edit.html',
        'text!/templates/workspace/annotator/document-note-form.html',
        'text!/templates/workspace/annotator/note-form.html',
        'ckeditor', 'ckeditor-jquery'],
       function($, _, Backbone, vent, Handlebars, noteEditTemplate,
                documentNoteFormTemplate, noteFormTemplate, CKEditor, ckEditorJquery) {
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
      _.bindAll(this, 'render', 'save', 'setDirty', 'annotate');
      var self = this;
      vent.on('document-note:change', function(documentNote) {
                                        self.documentNote = documentNote;
                                        self.render();
                                      });
      this.render();
    },

    render: function() {
      if (!this.documentNote) {
        return;
      }
      this.$el.html(this.template(this.documentNote))
              .effect('highlight', {color: 'lightblue'}, 500);
    },

    open: function(documentNote, options) {
      var self = this;
      console.log(documentNote);
      this.documentNote = documentNote;
      this.render();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-document-note').removeAttr('disabled');
    },
    
    annotate: function() {
      // TODO: implement
    }

    save: function() {
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
      var self = this;
      var request = {url: '/api/document-notes/' + this.documentNote.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(data) {
                       self.isDirty = false;
                       vent.trigger('document-note:change', data);
                     }};
      // TODO: Actual attaching to document
      if (!this.documentNote.id) {
        request.url = '/api/document-notes/';
        request.type = 'POST';
      }
      $.ajax(request);
    },
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
      _.bindAll(this, 'render', 'save', 'setDirty', 'open');
      var self = this;
      vent.on('note:change', function(note) {
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
      this.note = note;
      this.render();
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-note').removeAttr('disabled');
    },

    save: function() {
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
      var self = this;
      var request = {url: '/api/notes/' + this.note.id,
                     type: 'PUT',
                     dataType: 'json',
                     contentType: "application/json; charset=utf-8",
                     data: JSON.stringify(data),
                     success: function(data) {
                       vent.trigger('note:change', data);
                     }};
      if (!this.note.id) {
        request.url = '/api/notes/';
        request.type = 'POST';
      }
      $.ajax(request);
    }
  });

  var NoteEdit = Backbone.View.extend({
    template: Handlebars.compile(noteEditTemplate),

    events: {'click #save-document-note': 'saveDocumentNote',
             'click #save-note': 'saveNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'close', 'open', 'create', 'annotate', 'saveDocumentNote', 'saveNote');
      var self = this;
      vent.on('document:selection', function(documentId, selection) {
        if (self.$el.is(':visible')) {
          self.annotate(documentId, selection);
        }
      });
      vent.on('document-note:open', this.open);
      this.render();
    },

    render: function() {
      this.$el.html(this.template);
      this.noteForm = new NoteForm({el: this.$('form#note')});
      this.documentNoteForm = new DocumentNoteForm({el: this.$('form#document-note')});
    },

    close: function() {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      this.noteForm.close();
      this.documentNoteForm.close();
    },

    open: function(documentNote) {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      this.documentNoteForm.open(documentNote);
      $.getJSON('/api/notes/' + documentNote.note,
                this.noteForm.open)
    },

    create: function() {
      if (this.noteForm.isDirty || this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      noteForm.open({});
    },

    annotate: function(documentId, selection) {
      if (this.documentNoteForm.isDirty) {
        if (!confirm('U haz unsaved changes, continue?')) {
          return;
        }
      }
      this.documentNoteForm.open({document: documentId, fullSelection: selection.selection});
    },

    saveDocumentNote: function(evt) {
      evt.preventDefault();
      var self = this;
      if (this.documentNoteForm.documentNote.note) {
        this.documentNoteForm.save();
      } else {
        noteForm.save({success: function() {
                                  self.documentNoteForm.save();
                                }});
      }
    },

    saveNote: function(evt) {
      evt.preventDefault();
      this.noteForm.save();
    }
  });

  return NoteEdit;
});

