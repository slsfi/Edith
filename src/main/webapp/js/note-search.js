require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'handlebars', 'slickback', 'slickgrid', 'localize', 'text!/templates/header.html'],
          function($, _, Backbone, Handlebars, Slickback, Slick, localize, headerTemplate) {
    var headerTemplate = Handlebars.compile(headerTemplate);
    $('body').prepend(headerTemplate());

    var Note = Backbone.Model.extend({
      initialize: function() {
        var self = this;
        this.on('change', function() {
          self.dirty = true;
        });
      }
    });

    var NotesCollection = Slickback.PaginatedCollection.extend({
      model: Note,
      url: '/api/notes',

      setRefreshHints: function() {
        // Called when changing page size. TODO: Implement functionality?
      }
    });

    var notes = new NotesCollection();
    var columns = [{sortable: true, defaultSortAsc: true, id: 'lemma', name: localize('lemma-label'), field: 'lemma', editor: Slickback.TextCellEditor},
                   {sortable: true, maxWidth: 300, id: 'description', name: localize('description-label'), field: 'description'},
                   {sortable: true,  id: 'subtextSources', name: localize('subtextSources-label'), field: 'subtextSources'},
                   {sortable: true,  id: 'editedOn', name: localize('editedOn-label'), field: 'editedOn'},
                   {sortable: true,  id: 'basicForm', name: localize('basicForm-label'), field: 'term.basicForm'},
                   {id: 'meaning', name: localize('meaning-label'), field: 'term.meaning'}];
    var options = {
      formatterFactory: Slickback.BackboneModelFormatterFactory,
      editable: true,
      autoHeight: true,
      forceFitColumns: true,
      autoEdit: false
    };

    var grid = new Slick.Grid('#noteGrid', notes, columns, options);
    grid.setSelectionModel(new Slick.RowSelectionModel());
    var pager = new Slick.Controls.Pager(notes, grid, $('#notePager'));

    grid.onSort.subscribe(function(e, msg) {
      notes.extendScope({
        order: msg.sortCol.field,
        direction: (msg.sortAsc ? 'ASC' : 'DESC')
      });
      notes.fetchWithScope();
    });

    notes.onRowCountChanged.subscribe(function() {
      grid.updateRowCount();
      grid.render();
    });

    notes.onRowsChanged.subscribe(function() {
      grid.invalidateAllRows();
      grid.render();
    });

    notes.on('change', function() {
      // FIXME: Inline CSS bad
      $(grid.getActiveCellNode()).css('background', 'red');
    });

    notes.fetchWithPagination();

    var NoteSearch = Backbone.View.extend({
      events: {'keyup': 'search'},

      initialize: function() {
        _.bindAll(this, 'render');
      },

      search: function() {
        notes.extendScope({
          query: this.$el.val()
        });
        notes.fetchWithScope();
      }
    });

    var noteSearch = new NoteSearch({el: $('#noteSearch')});

    var DeleteNotes = Backbone.View.extend({
      events: {'click': 'remove'},

      initialize: function() {
        _.bindAll(this, 'remove');
      },

      remove: function() {
        var rows = grid.getSelectedRows();
        _.each(rows, function(row) {
          grid.getData().models[row].destroy();
        });
      }
    });

    var SaveNotes = Backbone.View.extend({
      events: {'click': 'save'},

      initialize: function() {
        _.bindAll(this, 'save');
      },

      save: function() {
        var dirty = notes.filter(function(note) { return note.dirty; });
        var reset = _.after(dirty.length, function() {
          notes.fetchWithPagination();
        });
        _(dirty).each(function(note) {
          note.save(null, {success: reset});
        });
      }
    });

    var deleteNotes = new DeleteNotes({el: $('#deleteNotes')});
    var saveNotes = new SaveNotes({el: $('#saveNotes')});
  });
});
