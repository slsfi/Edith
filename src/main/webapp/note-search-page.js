require.config(window.rconfig);

require([], function() {
  require(['jquery', 'underscore', 'backbone', 'slickback', 'slickgrid', 'text!templates/header.html'],
          function($, _, Backbone, Slickback, Slick, headerTemplate) {
    $('body').prepend(headerTemplate);

    var Note = Backbone.Model;
    var NotesCollection = Slickback.PaginatedCollection.extend({
      model: Note,
      url: '/api/notes'
    });
    
    var notes = new NotesCollection();
    var columns = [
                   {sortable: true, minWidth: 100, id: 'id', name: 'ID', field: 'id'},
                   {sortable: true, minWidth: 100, id: 'lemma', name: 'Lemma', field: 'lemma'},
                   {sortable: true, minWidth: 1400, id: 'description', name: 'Description', field: 'description'},
                   {sortable: true, minWidth: 100, id: 'subtextSources', name: 'Subtext sources', field: 'subtextSources'},
                   {sortable: true, minWidth: 100, id: 'editedOn', name: 'Edited on', field: 'editedOn'},
                   {sortable: true, minWidth: 100, id: 'basicForm', name: 'Basic form', field: 'term.basicForm'},
                   {minWidth: 100,id: 'meaning', name: 'Meaning', field: 'term.meaning'}
                   // TODO: Add 'poistettava'
                   ];
    var options = {
      formatterFactory: Slickback.BackboneModelFormatterFactory
    };
    var grid = new Slick.Grid('#noteGrid', notes, columns, options);
    var pager = new Slick.Controls.Pager(notes, grid, $('#notePager'));
    
    grid.onSort.subscribe(function(e, msg) {
      notes.extendScope({
        order: msg.sortCol.field,
        direction: (msg.sortAsc ? 'ASC' : 'DESC')
      });
      notes.fetchWithScope();
    });
    
    // TODO: Find out when these are called
    notes.bind('change', function(model, attributes) {
      model.save();
    });

    notes.onRowCountChanged.subscribe(function() {
      grid.updateRowCount();
      grid.render();
    });

    notes.onRowsChanged.subscribe(function() {
      grid.invalidateAllRows();
      grid.render();
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
    
    var noteSearch = new NoteSearch({el: $('#noteSearch')})
    
  });
});
