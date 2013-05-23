define(['jquery', 'underscore', 'backbone', 'vent', 'bootstrap',
        'text!/templates/workspace/annotator.html',
        'views/workspace/annotator/document',
        'views/workspace/annotator/note-list',
        'views/workspace/annotator/note-edit',
        'views/workspace/annotator/note-search'],
        function($, _, Backbone, vent, Bootstrap, template, Document, NoteList, NoteEdit, NoteSearch) {

  var AnnotatorView = Backbone.View.extend({
    events: {'shown a[data-toggle="tab"]': 'switchTabFromClick'},

    initialize: function() {
      _.bindAll(this, 'render', 'switchTabFromClick');
      var self = this;
      vent.on('document-note:open', function() {
        self.switchTab('note-edit');
        self.$('.nav-tabs a[data-target="note-edit"]').tab('show');
      });
      this.render();
    },

    render: function() {
      this.$el.html(template);
      new Document({el: this.$('#document')}),
      this.views = {noteList: new NoteList({el: this.$('#note-list')}),
                    noteEdit: new NoteEdit({el: this.$('#note-edit')}),
                    noteSearch: new NoteSearch({el: this.$('#note-search')})};
    },

    switchTab: function(tabName) {
      _.each(this.views, function(view) {
                           if (view.el.id === tabName) {
                             view.$el.show();
                           } else {
                             view.$el.hide();
                           }
                        });
    },

    switchTabFromClick: function(evt) {
      var target = evt.target.getAttribute('data-target');
      this.switchTab(target);
    }
  });

  return AnnotatorView;
});
