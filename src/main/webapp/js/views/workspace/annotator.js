define(['jquery', 'underscore', 'backbone', 'vent', 'bootstrap',
        'text!/templates/workspace/annotator.html',
        'views/workspace/annotator/document',
        'views/workspace/annotator/note-list'],
        function($, _, Backbone, vent, Bootstrap, template, Document, NoteList) {

  // TODO: Move into dedicated files and implement
  var NoteEdit = Backbone.View;
  var NoteSearch = Backbone.View;

  var AnnotatorView = Backbone.View.extend({
    events: {'shown a[data-toggle="tab"]': 'switchTab'},

    initialize: function() {
      _.bindAll(this, 'render', 'switchTab');
      this.render();
    },

    render: function() {
      this.$el.html(template);
      new Document({el: this.$('#document')}),
      this.views = {noteList: new NoteList({el: this.$('#note-list')}),
                    noteEdit: new NoteEdit({el: this.$('#note-edit')}),
                    noteSearch: new NoteSearch({el: this.$('#note-search')})};
    },

    switchTab: function(evt) {
      var target = evt.target.getAttribute('data-target');
      _.each(this.views, function(view) {
                           if (view.el.id === target) {
                             view.$el.show();
                           } else {
                             view.$el.hide();
                           }
                         });
    }
  });

  return AnnotatorView;
});
