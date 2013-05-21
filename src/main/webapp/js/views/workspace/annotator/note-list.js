define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars',
        'text!/templates/workspace/annotator/note-item.html'],
       function($, _, Backbone, vent, Handlebars, noteItemTemplate) {
  // TODO: What happens upon delete/re-render?
  var NoteListItem = Backbone.View.extend({
    tagName: 'li',

    template: Handlebars.compile(noteItemTemplate),

    events: {'click': 'open'},

    initialize: function() {
      _.bindAll(this, 'render', 'open');
      this.render();
    },

    render: function() {
      this.$el.html(this.template(this.options.data));
    },

    open: function(evt) {
      evt.preventDefault(); 
      vent.trigger('note:open', this.options.data.id);
    }
  });

  var NoteList = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render');
      vent.on('document:open annotation:created', this.render);
    },

    render: function(id) {
      this.$el.empty();
      var self = this;
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        _(data).each(function(note) {
          self.$el.append(new NoteListItem({data: note}).el);
        });
      });
    }
  });

  return NoteList;
});

