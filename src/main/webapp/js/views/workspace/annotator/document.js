define(['jquery', 'underscore', 'backbone', 'vent', 'handlebars'],
       function($, _, Backbone, vent, Handlebars) {
  var isInverseSelection = function(selection) {
    if (selection.anchorNode === selection.focusNode) {
      return selection.anchorOffset > selection.focusOffset;
    } else {
      return selection.anchorNode.compareDocumentPosition(selection.focusNode) == 2;
    }
  }

  var whitespaceRe = new RegExp(/\s+/g);

  var getBaseSelection = function() {
    var selection = null;
    if (window.getSelection) {
      selection = window.getSelection();
    } else if (document.getSelection) {
      selection = document.getSelection();
    } else {
      selection = document.selection.createRange().text;
    }
    if (selection.toString() === '') {
      return;
    }
    var startNode = selection.anchorNode;
    var endNode = selection.focusNode;
    var startOffset = selection.anchorOffset;
    var endOffset = selection.focusOffset;
    var selectionString = selection.toString().trim();
    if (isInverseSelection(selection)) {
      startNode = selection.focusNode;
      endNode = selection.anchorNode;
      startOffset = selection.focusOffset;
      endOffset = selection.anchorOffset;
    }
    if (endOffset === 0) {
      // FIXME: This is not exhaustive (might be parent or sibling even more on the left)
      endNode = endNode.previousSibling;
      endOffset = endNode.textContent.length - 1;
    }
    var i = 0;
    while (whitespaceRe.test(selection.toString().charAt(i))) {
      --startOffset;
      --endOffset;
      ++i;
    }
    i = selection.toString().length - 1;
    while (whitespaceRe.test(selection.toString().charAt(i))) {
      --endOffset;
      --i;
    }
    return {startNode: startNode,
            endNode: endNode,
            startOffset: startOffset,
            endOffset: endOffset,
            selectionString: selectionString};
  }

  var getCharIndex = function(str, char, offset) {
    var prevStr = str.substring(0, offset);
    var idx = -1;
    for (var i = 0; i < prevStr.length; ++i) {
      if (prevStr.charAt(i) === char) {
        ++idx
      }
    }
    return ++idx;
  }

  var previousSiblingsToString = function(node) {
    var sibling = node.previousSibling;
    if (sibling) {
      var str = '';
      if (sibling.nodeType === 3 || sibling.className.indexOf('notecontent') !== -1) {
        str = sibling.textContent;
      }
      return previousSiblingsToString(sibling) + str;
    }
    return '';
  }

  var documentNoteTemplate = Handlebars.compile(documentNoteTemplate);
  var DocumentView = Backbone.View.extend({
    events: {'mouseup': 'selectionChange',
             'click .noteanchor': 'selectNote'},

    initialize: function() {
      _.bindAll(this, 'render', 'selectionChange', 'selectNote', 'getSelection');
      var self = this;
      vent.on('document:open annotation:change', function(id) {
        self.documentId = id;
        self.render(id);
      });
      vent.on('document-note:select', function(id, skipScroll) {
        if (skipScroll) {
          return;
        }
        var elem = self.$('#start' + id);
        if (elem.size() == 1) {
          elem[0].scrollIntoView(true);
        }
      });
      vent.on('note:new', function() {
        var selection = self.getSelection();
        if (selection) {
          vent.trigger('document-note:create', self.documentId, selection);
        }
      });
    },

    render: function(id) {
      var self = this;
      // FIXME: We know better
      $.get('/api/documents/' + id + '/raw', function(data) {
        self.$el.html(data);
      });
    },
    
    selectNote: function(evt) {
      var id = parseInt($(evt.target).attr('id').substring(3));
      vent.trigger('document-note:select', id, true);
    },

    getSelection: function() {
      var baseSelection = getBaseSelection();
      if (!baseSelection) {
        return;
      }
      var str = baseSelection.selectionString;
      var startChar = str.charAt(0);
      var endChar = str.charAt(str.length - 1);
      var previousFromStart = previousSiblingsToString(baseSelection.startNode);
      var additionalStartOffset = previousFromStart.length;
      var previousFromEnd = previousSiblingsToString(baseSelection.endNode);
      var additionalEndOffset = previousFromEnd.length;
      var startCharIndex = getCharIndex(previousFromStart + baseSelection.startNode.textContent, startChar,
          baseSelection.startOffset + additionalStartOffset);
      var endCharIndex = getCharIndex(previousFromEnd + baseSelection.endNode.textContent, endChar,
          (baseSelection.endOffset - 1) + additionalEndOffset);
      var startParent = baseSelection.startNode.parentNode;
      var endParent = baseSelection.endNode.parentNode;
      var selection = {selection: str,
//                       startChar: startChar,
                       startCharIndex: startCharIndex,
                       startNode: startParent.id || startParent.getAttribute('data-node'),
                       endNode: endParent.id || endParent.getAttribute('data-node'),
//                       endChar: endChar,
                       endCharIndex: endCharIndex};
      return selection;
    },

    selectionChange: function() {
      var selection = this.getSelection();
      vent.trigger('document:selection', this.documentId, selection);
    }
  });

  return DocumentView;
});

