define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars', 'slickback',
        'slickgrid', 'moment', 'localize', 'spinner', 'raw-selection',
        'text!templates/workspace/annotator/note-search.html',
        'views/workspace/annotator/metadata-field-select'],
       function($, _, Backbone, vent, Handlebars, Slickback, Slick, moment,
                localize, spinner, rawSelection, searchTemplate, MetadataFieldSelect) {
  
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
    url: 'api/document-notes/query',
    
    setRefreshHints: function() {
      // TODO
    },

    sync: function(method, coll, options) {
      var data = options.data;
      if (data.per_page) {
        data.perPage = data.per_page;
        delete data.per_page;
      }

      // Backbone 1.0 compatibility
      options.reset = true;

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
    url: 'api/users'
  });
  
  var users = new UsersCollection();
  
  var EditedByFormatter = function(row, cell, value, columnDef, data) {
    var value = data.get('note').editedOn;
    var editedBy = data.get('note').lastEditedBy.username;
    return moment.unix(value / 1000).format('D.M.YY') + ' (' + editedBy + ')';

  };
  
  var TypesFormatter = function(row, cell, value, columnDef, data) {
    var types = data.get('note').types;
    return _.map(types, localize).join(', ');
  };
  
  var DescriptionFormatter = function(row, cell, value, columnDef, data) {
    return $(data.get('note').description).text();
  };

  var StatusFormatter = function(row, cell, value, columnDef, data) {
    return localize(data.get('note').status);
  };

  var allColumns = [
      {sortable: true, id: 'shortenedSelection', width: 120, name: localize('shortenedSelection-label'), 
        field: 'shortenedSelection'},
      {sortable: true, id: 'fullSelection', name: localize('fullSelection-label'), field: 'fullSelection'},
      {sortable: false, id: 'types', name: localize('type-label'), field: 'note.types', formatter: TypesFormatter},
      {sortable: true, id: 'description', name: localize('description-label'), field: 'note.description', formatter: DescriptionFormatter},
      {sortable: true, id: 'editedOn', name: localize('editedOn-label'), field: 'note.editedOn', formatter: EditedByFormatter},
      {sortable: true, id: 'status', name: localize('status-label'), field: 'note.status', formatter: StatusFormatter},
      {sortable: true, id: 'basicForm', name: localize('basicForm-label'), field: 'note.term.basicForm'},
      {sortable: true, id: 'document', name: localize('document-label'), field: 'document.title'},
      {sortable: false, id: 'comment', name: localize('comment-label'), field: 'note.comment.message'}];
  
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
      
      var $pagers = this.options.$pagers;
      $pagers.each(function(idx, pager) {
        new Slick.Controls.Pager(documentNotes, grid, $(pager));
      });
      $pagers.find('.ui-icon').click(function(evt) {
        var $el = $(evt.target); 
        if (!$el.hasClass('ui-state-disabled') && !$el.hasClass('ui-icon-lightbulb')) {
          spinner('document-note-search:reset'); 
        }
      });
      
      grid.onSort.subscribe(function(e, msg) {
        documentNotes.extendScope({
          order: msg.sortCol.field,
          direction: (msg.sortAsc ? 'ASC' : 'DESC')
        });
        spinner('document-note-search:reset');
        documentNotes.fetchWithScope();
      });

      grid.onClick.subscribe(function(e, args) {
        $('button.grid-action').removeAttr('disabled');
        vent.trigger('document:reset-raw-selection');
      });
      
      documentNotes.onRowCountChanged.subscribe(function() {
        grid.updateRowCount();
        grid.render();
      });
      
      documentNotes.onRowsChanged.subscribe(function() {
        grid.invalidateAllRows();
        grid.render();
      });
    }
    
  });
  
  var userOption = Handlebars.compile('<option value="{{id}}">{{username}}</option>');
  
  var dateFields = ['createdAfter', 'createdBefore', 'editedAfter', 'editedBefore'];
    
  var NoteSearch = Backbone.View.extend({
    events: {'keyup form input[type="text"]': 'handleTextInputKeyUp',
             'change form input[type!="text"]': 'handleNonTextInputChange',
             'change form select[name="creators"]': 'handleNonTextInputChange',
             'click form .search': 'handleSearchButton',
             'click .annotate': 'annotate',
             'click .select': 'select',
             'click .comment': 'comment',
             'click .edit-note': 'editNote'},
    
    initialize: function() {
      var self = this;
      _.bindAll(this, 'filterColumnsAccordingToSelection', 'handleSearchButton', 'handleNonTextInputChange', 'handleTextInputKeyUp', 'render', 'search', 'annotate', 'select', 'comment', 'editNote');
      
      vent.on('tab:open', function(view) {
        if (view === self && !self.initialized) {
          spinner('document-note-search:reset');
          documentNotes.fetchWithPagination();
          self.initialized = true;
        }
      });
      
      vent.on('metadata-field-select:change', function(columns) {
        if (self.$el.is(':visible')) {
          self.filterColumnsAccordingToSelection(columns);
        }
      });
      
      vent.on('document:open', function(documentId) {
        self.documentId = documentId;
      });

      vent.on('documents:reset document:deleted document:renamed', function() {
        self.$('.directory-tree').dynatree('getTree').reload();
      });

      users.on('reset', this.render);
      users.fetch({reset: true});
    },

    handleTextInputKeyUp: function(event) {
      if (event.keyCode == 13) {
        this.$('.search').click();
      }
    },

    handleNonTextInputChange: function(event) {
      this.$('.search').click();
    },

    handleSearchButton: function(event) {
      event.preventDefault();
      this.search();
    },

    filterColumnsAccordingToSelection: function(columns) {
      this.gridView.grid.setColumns(_.filter(allColumns, function(col) {
        return _.contains(columns, col.id);
      }));
    },

    search: function() {
      spinner('document-note-search:reset');
      var arr = this.$('form').serializeArray();
      var data = _(arr).reduce(function(acc, field) {
             if (field.value) {
               if (field.name == 'creators' || field.name == 'types' || field.name == 'filters') {
                 if (!acc[field.name]) {
                   acc[field.name] = [];
                 }
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

      // clean data to make sure documentNotes.extendScope works properly

      if (!data.filters) {
        data.filters = [];
      }

      if (!data.types) {
        data.types = [];
      }

      if (!data.language) {
        data.language = null;
      }

      if (!data.status) {
        data.status = null;
      }

      // tree nodes
      var nodes = this.$('.directory-tree').dynatree('getSelectedNodes');
      data.documents = _.map(nodes, function(n) { return n.data.documentId; });
      data.paths = _.map(nodes, function(n) { return encodeURIComponent(n.data.path); });
      
      documentNotes.extendScope(data);
      documentNotes.fetchWithScope();
    },
  
    render: function() {
      var self = this;

      this.$el.html(searchTemplate());
      this.gridView = new GridView({el: this.$('.documentNoteGrid'), $pagers: this.$('.documentNotePager')});     
      this.metadataSelect = new MetadataFieldSelect({el: this.$('#search-results-metadata'), defaultSelection: ['description', 'document']});     
      this.filterColumnsAccordingToSelection(this.metadataSelect.getColumns());

      var userOpts = _.map(users.toJSON(), userOption).join('');
      this.$('.creators').html(userOption({}) + userOpts); 



      this.$('.date').datepicker({ dateFormat: localize('dateformat') });
      
      this.$('.directory-tree').dynatree({
        checkbox: true,
        selectMode: 3,
        initAjax: {
          url: 'api/files'
        },

        onLazyRead: function(node) {
          node.appendAjax({
            url: 'api/files/',
            data: 'path=' + encodeURIComponent(node.data.path),
            success: function(node) {
           if (node.isSelected()) {
                $.each(node.childList, function(){
                  this._select(true, false, true);
                });
              }
            }
          });
        },

        onDblClick: function(node) {
          if (!node.data.isFolder) {
            vent.trigger('route:change', 'documents/' + node.data.documentId);
          }
        },

        onSelect: function() {
          self.search();
        }

      });

      this.$('#advancedSearch .multiselect').multiselect({
        buttonText: function(options, select) {
          if (options.length == 0) {
            return localize('nothing-selected');
          } else if (options.length > 4) {
            return options.length + ' ' + localize('n-selected') + ' <b class="caret"></b>';
          } else {
            var selected = '';
            options.each(function() {
              selected += $(this).text() + ', ';
            });
            return selected.substr(0, selected.length -2) + ' <b class="caret"></b>';
          }
        },
      });
    },

    annotate: function() {
      var selection = rawSelection();
      if (selection.toString() === '') {
        alert(localize('no-text-selected'));
      } else {
        vent.trigger('note:link-existing', this.gridView.getSelected().note);
      }
    },

    select: function() {
      var documentNote = this.gridView.getSelected();
      if (this.documentId === documentNote.document.id) {
        vent.trigger('document-note:select', documentNote.id);
      } else if (confirm(localize('confirm-document-change'))) {
        var select = _.after(2, function(documentId) {
          vent.trigger('document-note:select', documentNote.id);
        });
        vent.once('document:loaded', select);
        vent.once('document-notes:loaded', select);
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
    }
  });
  
  return NoteSearch;   
  
});
