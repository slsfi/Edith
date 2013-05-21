define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-edit.html'],
       function($, _, Backbone, vent, Handlebars, template) {
  var NoteEdit = Backbone.View.extend({
    events: {'click #save-document-note': 'saveDocumentNote',
             'click #save-note': 'saveNote'},

    template: Handlebars.compile(template),

    initialize: function() {
      _.bindAll(this, 'render', 'saveNote', 'saveDocumentNote');
      var self = this;
      vent.on('note:open', function(id) {
                             $.getJSON('/api/document-notes/' + id,
                                       {note: true},
                                       self.render);
                           });
    },

    render: function(data) {
      this.documentNote = data.documentNote;
      this.documentNote.note = data.note;
      this.$el.html(this.template(this.documentNote));
    },

    saveDocumentNote: function(evt) {
      evt.preventDefault();
      var arr = this.$('#document-note').serializeArray();
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
                console.log(data);
              }
      });
    },

    saveNote: function(evt) {
      evt.preventDefault();
      console.log('saving note');
    }
  });

  return NoteEdit;
});

