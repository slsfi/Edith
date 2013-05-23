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

    events: {'click #save-document-note': 'saveDocumentNote',
             'keyup input': 'setDirty'},

    initialize: function() {
      _.bindAll(this, 'render', 'saveDocumentNote', 'setDirty');
      var self = this;
      vent.on('document-note:open document-note:change',
              function(documentNote) {
                // TODO: Hook into 'unload' event
                if ((self.isDirty || self.hasDirtyChildren) &&
                    !confirm('U haz unsaved changes, continue?')) {
                  return;
                }
                vent.trigger('note:open', documentNote.note);
                self.documentNote = documentNote;
                self.render();
              });
      vent.on('note:dirty', function() { self.hasDirtyChildren = true; });
      vent.on('note:change', function() { self.hasDirtyChildren = false; });
    },

    render: function() {
      this.$el.html(this.template(this.documentNote))
              .effect('highlight', {color: 'lightblue'}, 500);
    },

    setDirty: function() {
      this.isDirty = true;
      this.$('#save-document-note').removeAttr('disabled');
    },
    
    saveDocumentNote: function(evt) {
      evt.preventDefault();
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
      $.ajax({url: '/api/document-notes/' + this.documentNote.id,
              type: 'PUT',
              dataType: 'json',
              contentType: "application/json; charset=utf-8",
              data: JSON.stringify(data),
              success: function(data) {
                self.isDirty = false;
                vent.trigger('document-note:change', data);
              }});
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
    events: {'click #save-note': 'saveNote',
             'keyup input': 'setDirty',
             'change input': 'setDirty',
             'change select': 'setDirty'},
  
    template: Handlebars.compile(noteFormTemplate),

    initialize: function() {
      _.bindAll(this, 'render', 'saveNote', 'setDirty');
      var self = this;
      vent.on('note:change', function(note) {
                               self.note = note;
                               self.render();
                             });
      vent.on('note:open', function(noteId) {
                             $.getJSON('/api/notes/' + noteId,
                                       function(note) {
                                         self.note = note;
                                         self.render();
                                       });
                           });
      vent.on('note:create', function() {
                               self.note = {};
                               self.render();
                             });
    },

    render: function() {
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
    
    setDirty: function() {
      console.log('dirty')
      this.$('#save-note').removeAttr('disabled');
      vent.trigger('note:dirty');
    },

    saveNote: function(evt) {
      evt.preventDefault();
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

    initialize: function() {
      _.bindAll(this, 'render');
      var self = this;
      this.render();
    },

    render: function() {
      this.$el.html(this.template);
      new DocumentNoteForm({el: this.$('form#document-note')});
      new NoteForm({el: this.$('form#note')});
    }
  });

  return NoteEdit;
});

