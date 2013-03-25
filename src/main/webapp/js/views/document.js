define(['jquery', 'underscore', 'backbone', 'js/vent', 'handlebars',
        'text!/templates/document-note.html'],
  function($, _, Backbone, vent, Handlebars, documentNoteTemplate) {
  var documentNoteTemplate = Handlebars.compile(documentNoteTemplate);
  var DocumentView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      vent.on('document:open', this.render);
    },
    
    render: function(id) {
      var self = this;
      // FIXME: We know better
      $.get('/api/documents/' + id + '/raw', function(data) {
        self.$('#documentView').html(data);
      });
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        _(data).each(function(documentNote) {
          self.$('#documentNoteListing').append(documentNoteTemplate(documentNote));
        });
      });
    }
  });
  
  return DocumentView;
});