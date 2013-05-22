define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-edit.html',
        'text!/templates/workspace/annotator/document-note-form.html',
        'text!/templates/workspace/annotator/note-form.html'],
       function($, _, Backbone, vent, Handlebars, noteEditTemplate,
                documentNoteFormTemplate, noteFormTemplate) {
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

    events: {'click #save-document-note': 'saveDocumentNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'saveDocumentNote');
      var self = this;
      vent.on('document-note:open', function(documentNoteId) {
                                      $.getJSON('/api/document-notes/' + documentNoteId,
                                                function(documentNote) {
                                                  vent.trigger('note:open', documentNote.note);
                                                  self.documentNote = documentNote;
                                                  self.render();
                                                });
                                    });
    },

    render: function() {
      this.$el.html(this.template(this.documentNote));
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
      $.ajax({url: '/api/document-notes/' + this.documentNote.id,
              type: 'PUT',
              dataType: 'json',
              contentType: "application/json; charset=utf-8",
              data: JSON.stringify(data),
              success: function(data) {
                vent.trigger('document-note:open', data.id);
              }});
    },
  });

  var NoteForm = Backbone.View.extend({
    events: {'click #save-note': 'saveNote'},

    template: Handlebars.compile(noteFormTemplate),

    initialize: function() {
    _.bindAll(this, 'render', 'saveNote');
    var self = this;
    vent.on('note:open', function(id) {
                           $.getJSON('/api/notes/' + id,
                                     function(note) {
                                       self.note = note;
                                       self.render();
                                     });
                         });
    },

    render: function() {
      this.$el.html(this.template(this.note));
    },

    saveNote: function(evt) {
      evt.preventDefault();
      console.log('saving note');
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
      $.ajax({url: '/api/notes/' + this.note.id,
        type: 'PUT',
        dataType: 'json',
        contentType: "application/json; charset=utf-8",
        data: JSON.stringify(data),
        success: function(data) {
          vent.trigger('note:open', data.id);
        }});
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

