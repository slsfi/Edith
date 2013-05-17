define(['jquery', 'underscore', 'backbone', 'vent',
        'text!/templates/workspace/annotator.html',
        'views/workspace/annotator/document',
        'views/workspace/annotator/note-list'],
  function($, _, Backbone, vent, template, Document, NoteList) {
  var AnnotatorView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      this.render();
    },

    render: function() {
      this.$el.html(template);
      this.documentView = new Document({el: this.$('#document')});
      this.noteListView = new NoteList({el: this.$('#note-list')});
    }
  })

  return AnnotatorView;
});