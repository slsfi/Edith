define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'slickback', 'slickgrid', 'localize',
        'text!/templates/workspace/annotator/note-search.html'],
       function($, _, Backbone, vent, Handlebars, Slickback, Slick, localize, searchTemplate) {
  
  var DocumentNote = Backbone.Model.extend({
    initialize: function() {
      var self = this;
      this.on('change', function() {
        self.dirty = true;
      });
    }
  });
  
  var DocumentNotesCollection = Slickback.PaginatedCollection.extend({
    model: DocumentNote,
    url: '/api/document-notes',
    
    setRefreshHints: function() {
      // TODO
    }
  });
  
  var documentNotes = new DocumentNotesCollection();
  
  var columns = [{sortable: true, id: 'fullSelection', name:'Full selection', field: 'fullSelection'},
                 {sortable: true, id: 'shortenedSelection', name: 'Shortened selection', field: 'shortenedSelection'},
                 {sortable: true, id: 'lemmaPosition', name: 'Lemma position', field: 'lemmaPosition'}];
  // TODO note and term columns
  
  var options = {
    formatterFactory: Slickback.BackboneModelFormatterFactory,
    editable: true,
    autoHeight: true,
    forceFitColumns: true,
    autoEdit: false
  };
  
  var NoteSearch = Backbone.View.extend({
    
    events: {'keyup #documentNoteSearch': 'search'},
    
    initialize: function() {
      _.bindAll(this, 'render', 'search');
      this.render();
    },
    
    search: function() {
      documentNotes.extendScope({
        query: this.$("#documentNoteSearch").val()
      });
      documentNotes.fetchWithScope();
    },
  
    render: function() {
      this.$el.html(searchTemplate);
      
      var grid = new Slick.Grid('#documentNoteGrid', documentNotes, columns, options);
      grid.setSelectionModel(new Slick.RowSelectionModel());
      var pager = new Slick.Controls.Pager(documentNotes, grid, this.$('#documentNotePager'));
      
      grid.onSort.subscribe(function(e, msg) {
        documentNotes.extendScope({
          order: msg.sortCol.field,
          direction: (msg.sortAsc ? 'ASC' : 'DESC')
        })
        documentNotes.fetchWithScope();
      });
      
      documentNotes.onRowCountChanged.subscribe(function() {
        grid.updateRowCount();
        grid.render();
      });
      
      documentNotes.onRowsChanged.subscribe(function() {
        grid.invalidateAllRows();
        grid.render();
      });
      
      documentNotes.on('change', function() {
        $(grid.getActiveCellNode()).css('background', 'red');
      });
      
      documentNotes.fetchWithPagination();
    }
    
  });
  
  return NoteSearch;   
  
});