define(['jquery', 'underscore', 'backbone', 'js/vent', 'handlebars',
        'text!/templates/documents-page/document-note.html'],
       function($, _, Backbone, vent, Handlebars, documentNoteTemplate) {
  var isInverseSelection = function(selection) {
    if (selection.anchorNode === selection.focusNode) {
      return selection.anchorOffset > selection.focusOffset;
    } else {
      return selection.anchorNode.compareDocumentPosition(selection.focusNode) == 2;
    }
  }

  var whitespaceRe = new RegExp(/\s+/g);

  var getSelection = function() {
    var selection = null;
    if (window.getSelection) {
      selection = window.getSelection();
    } else if (document.getSelection) {
      selection = document.getSelection();
    } else {
      selection = document.selection.createRange().text;
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
      if (sibling.nodeType === 3) {
        str = sibling.textContent;
      }
      return previousSiblingsToString(sibling) + str;
    }
    return '';
  }

  var documentNoteTemplate = Handlebars.compile(documentNoteTemplate);
  var DocumentView = Backbone.View.extend({
    events: {'mouseup': 'selectionChange'},

    initialize: function() {
      _.bindAll(this, 'render', 'selectionChange');
      var self = this;
      vent.on('document:open', function(id) {
        self.documentId = id;
        self.render(id);
      });
    },
    
    render: function(id) {
      var self = this;
      // FIXME: We know better
      $.get('/api/documents/' + id + '/raw', function(data) {
        self.$('#documentView').html(data);
        self.$('.notecontent').css('background', 'lightblue');
      });
      $.get('/api/documents/' + id + '/document-notes', function(data) {
        _(data).each(function(documentNote) {
          self.$('#documentNoteListing').append(documentNoteTemplate(documentNote));
        });
      });
    },

    selectionChange: function() {
      var baseSelection = getSelection();
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
                       startNode: startParent.id || startParent.attributes.getNamedItem('data-node').nodeValue,
                       endNode: endParent.id || endParent.attributes.getNamedItem('data-node').nodeValue,
//                       endChar: endChar,
                       endCharIndex: endCharIndex};
      var self = this;
      if (str && confirm('Annotate?')) {
        $.ajax('api/documents/' + this.documentId + '/document-notes',
               {type: 'post',
                contentType: "application/json; charset=utf-8",
                data: JSON.stringify({text: selection}),
                success: function(resp) {
                  self.render(self.documentId);
                }});
      }
    }
  });
  
  return DocumentView;
});