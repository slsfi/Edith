define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'slickback',
        'slickgrid', 'moment', 'localize', 'spinner',
        'text!/templates/workspace/annotator/note-search.html',
        'views/workspace/annotator/metadata-field-select'],
       function($, _, Backbone, vent, Handlebars, Slickback, Slick, moment,
                localize, spinner, searchTemplate, MetadataFieldSelect) {
  
  var searchTemplate = Handlebars.compile(searchTemplate);
  
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
    url: '/api/document-notes/query',
    
    setRefreshHints: function() {
      // TODO
    },
    
    sync: function(method, coll, options) {
      var data = options.data;
      if (data.per_page) {
        data.perPage = data.per_page;
        delete data.per_page;
      }
      $.ajax('api/document-notes/query',
             {type: 'post',
              contentType: 'application/json;charset=utf-8',
              data: JSON.stringify(data),
              success: function(response) {
                         vent.trigger('document-note-search:reset');
                         options.success(response);
                       }
      });
    }
  });
  

  var documentNotes = new DocumentNotesCollection();
  
  var UsersCollection = Backbone.Collection.extend({
    model: Backbone.Model,
    url: '/api/users'
  });
  
  var users = new UsersCollection();
  users.fetch();
  
  var EditedByFormatter = function(row, cell, value, columnDef, data) {
    var value = data.get('note').editedOn;
    var editedBy = data.get('note').lastEditedBy.username;
    return moment.unix(value / 1000).format("D.M.YY") + " (" + editedBy + ")";

  };
  
  var TypesFormatter = function(row, cell, value, columnDef, data) {
    var types = data.get('note').types;
    return _.map(types, localize).join(", ");
  };
  
  var allColumns = [
      {sortable: true, id: 'shortenedSelection', width: 120, name: localize('shortenedSelection-label'), 
        field: 'shortenedSelection'},
      {sortable: true, id: 'fullSelection', name: localize('fullSelection-label'), field: 'fullSelection'},
      {sortable: false, id: 'types', name: localize('types-label'), field: 'note.types', formatter: TypesFormatter},
      {sortable: true, id: 'description', name: localize('description-label'), field: 'note.term.meaning'},
      {sortable: true, id: 'editedOn', name: localize('editedOn-label'), field: 'note.editedOn', formatter: EditedByFormatter},
      {sortable: true, id: 'status', name: localize('status-label'), field: 'note.status'},
      {sortable: true, id: 'document', name: localize('document-label'), field: 'document.title'},
      {sortable: false, id: 'comment', name: localize('comment'), field: 'note.comment.message'}];
  
  var options = {
    formatterFactory: Slickback.BackboneModelFormatterFactory,
    autoHeight: true,
    forceFitColumns: true,
    autoEdit: false,
    defaultColumnWidth: 100
  };
  
  var GridView = Backbone.View.extend({
    initialize: function() {
      _.bindAll(this, 'render', 'getSelected');
      this.render();
    },

    getSelected: function() {
      return (this.grid.getData().models[this.grid.getSelectedRows()[0]]).toJSON();
    },
    
    render: function() {
      var grid = new Slick.Grid(this.$el, documentNotes, allColumns, options);
      this.grid = grid;
      grid.setSelectionModel(new Slick.RowSelectionModel());
      
      var pager = new Slick.Controls.Pager(documentNotes, grid, this.options.$pager);
      
      grid.onSort.subscribe(function(e, msg) {
        documentNotes.extendScope({
          order: msg.sortCol.field,
          direction: (msg.sortAsc ? 'ASC' : 'DESC')
        });
        spinner('document-note-search:reset');
        documentNotes.fetchWithScope();
      });

      grid.onClick.subscribe(function() {
        self.$('button').removeAttr('disabled');
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
    }
    
  });
  
  var userOption = Handlebars.compile("<option value='{{id}}'>{{username}}</option>");
  
  var dateFields = ['createdAfter', 'createdBefore', 'editedAfter', 'editedBefore'];
    
  var NoteSearch = Backbone.View.extend({
    events: {'keyup .search': 'search',
             'change form input': 'search',
             'change form select': 'search',
             'click .select': 'select',
             'click .comment': 'comment',
             'click .edit-note': 'editNote',
             'click .edit-instance': 'editInstance'},
    
    initialize: function() {
      var self = this;
      _.bindAll(this, 'render', 'search', 'select', 'comment', 'editNote', 'editInstance');
      vent.on('tab:open', function(view) {
        if (view === self && !self.initialized) {
          spinner('document-note-search:reset');
          documentNotes.fetchWithPagination();
          self.initialized = true;
        }
      });
      vent.on('metadata-field-select:change', function(columns) {
        if (self.$el.is(':visible')) {
          self.gridView.grid.setColumns(_.filter(allColumns, function(col) {
            return _.contains(columns, col.id);
          }));
        }
      });
      vent.on('document:open', function(documentId) {
        self.documentId = documentId;
      });
      this.render();
    },
    
    search: function() {
      spinner('document-note-search:reset');
      var arr = this.$("form").serializeArray();
      var data = _(arr).reduce(function(acc, field) {
             if (field.value) {
               if (field.name == 'creators' || field.name == 'types') {
                 if (!acc[field.name]) acc[field.name] = [];
                 acc[field.name].push(field.value);
               } else if (dateFields.indexOf(field.name) > -1) {  
                 acc[field.name] = moment(field.value, 'DD.MM.YYYY').valueOf();
               } else {
                 acc[field.name] = field.value;       
               }  
             } else {
               acc[field.name] = null;
             }     
             return acc;
           }, {});
      
      // tree nodes
      var nodes = this.$(".directory-tree").dynatree("getSelectedNodes");
      data.documents = _.map(nodes, function(n) { return n.data.documentId; });
      
      documentNotes.extendScope(data);
      spinner(''); 
      documentNotes.fetchWithScope();
    },
  
    render: function() {
      var self = this;
      this.$el.html(searchTemplate());
      this.gridView = new GridView({el: this.$('.documentNoteGrid'), $pager: this.$('.documentNotePager')});     
      
      var cb = function() { 
        var userOpts = _.map(users.toJSON(), userOption).join("");
        this.$(".creators").html(userOption({}) + userOpts); 
      };
      if (users.length > 0) cb(); else users.fetch({success: cb});
      
      this.$(".date").datepicker({ dateFormat: localize('dateformat') });
      
      new MetadataFieldSelect({el: this.$('.metadata-field-select')});
      
      this.$('.directory-tree').dynatree({
        checkbox: true,
        selectMode: 3,
        initAjax: {
          url: '/api/files'
        },

        onLazyRead: function(node) {
          node.appendAjax({
            url: '/api/files/',
            data: 'path=' + node.data.path
          });
        },
        
        onSelect: function() {
          self.search();
        }
        
      });
    },

    select: function() {
      var documentNote = this.gridView.getSelected();
      if (this.documentId === documentNote.document.id) {
        vent.trigger('document-note:select', documentNote.id);
      } else if (confirm(localize('confirm-document-change'))) {
        vent.once('document:loaded', function(documentId) {
          vent.trigger('document-note:select', documentNote.id);
        });
        vent.trigger('route:change', 'documents/' + documentNote.document.id);
      }
    },
    
    comment: function() {
      var documentNote = this.gridView.getSelected();
      vent.trigger('comment:edit', documentNote.note.id, documentNote.note.comment);
    },

    editNote: function() {
      var documentNote = this.gridView.getSelected();
      vent.trigger('note:open', documentNote.note);
    },

    editInstance: function() {
      var documentNote = this.gridView.getSelected();
      if (this.documentId === documentNote.document.id) {
        vent.trigger('document-note:open', documentNote);
      } else if (confirm(localize('confirm-document-change'))) {
        vent.once('document:loaded', function(documentId) {
          vent.trigger('document-note:select', documentNote.id);
        });
        vent.trigger('route:change', 'documents/' + documentNote.document.id);
        vent.trigger('document-note:open', documentNote);
      }
    }
  });
  
  return NoteSearch;   
  
});
