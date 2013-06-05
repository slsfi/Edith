define(['jquery', 'underscore', 'backbone', 'handlebars', 'vent', 'bootstrap', 'localize',
        'text!/templates/workspace/annotator.html',
        'views/workspace/annotator/document',
        'views/workspace/annotator/note-list',
        'views/workspace/annotator/note-edit',
        'views/workspace/annotator/note-search',
        'views/workspace/annotator/comment-edit'],
        function($, _, Backbone, Handlebars, vent, Bootstrap, localize, template,
                 Document, NoteList, NoteEdit, NoteSearch, CommentEdit) {

  var template = Handlebars.compile(template);
  
  var AnnotatorView = Backbone.View.extend({
    events: {'click a[data-toggle="tab"]': 'switchTabFromClick'},

    initialize: function() {
      _.bindAll(this, 'render', 'switchTabFromClick');
      var self = this;
      vent.on('document-note:open note:open note:create', function() {
        if (!self.noteEditInitialized) {
          self.$('.nav-tabs').append('<li><a href="#" data-target="note-edit" data-toggle="tab">' +
                                     localize('edit') +
                                     '</a></li>');
          self.noteEditInitialized = true;
        }
        self.switchTab('note-edit');
        self.$('.nav-tabs a[data-target="note-edit"]').tab('show');
      });
      this.render();
    },

    render: function() {
      this.$el.html(template());
      new Document({el: this.$('#document')}),
      this.views = {noteList: new NoteList({el: this.$('#note-list')}),
                    noteEdit: new NoteEdit({el: this.$('#note-edit')}),
                    noteSearch: new NoteSearch({el: this.$('#note-search')})};
      this.commentEdit = new CommentEdit({el: this.$('#comment-edit')});
    },

    switchTab: function(tabName) {      
      _.each(this.views, function(view) {
                           if (view.el.id === tabName) {
                             view.$el.show();
                             vent.trigger('tab:open', view);
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
