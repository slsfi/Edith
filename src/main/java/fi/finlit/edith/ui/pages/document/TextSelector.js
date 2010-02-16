jQuery.noConflict();

var TextSelector = {
		started : false,
		startId : null,
		endId : null,
		startIndex: null,
		endIndex : null,

		// tested
		getOccurrenceInString : function(str, substr, minIndex) {	
			var occurrence = 0;
			for (var i = 0; i < str.length; ++i) {
				if (str.substring(i).indexOf(substr) == 0) {
					++occurrence;
					if (i >= minIndex) {
						return occurrence;
					}
				}
			}
			return 0;
		},

		// tested
		getOccurrences : function(str, substr) {
			var occurrence = 0;
			for (var i = 0; i < str.length; ++i) {
				if (str.substring(i).indexOf(substr) == 0) {
					++occurrence;
				}
			}
			return occurrence;
		},

		// tested
		prevAllString : function(element) {
			var text = new Array();
			element = element.previousSibling;
			while (element != null) {
				text.push(element.nodeValue != null ? element.nodeValue : element.innerHTML);
				element = element.previousSibling;
			}
			text.reverse();
			return text.join("");			
		},

		// tested
		getOccurrenceInElement : function(element, offset, substr) {
			var prevOccurrences = this.getOccurrences(this.prevAllString(element.get(0)), substr);
			if (element.parent().hasClass("notecontent")){
				prevOccurrences += this.getOccurrences(this.prevAllString(element.parent().get(0)), substr);
			}
			var occurrence = this.getOccurrenceInString(element.text(), substr.substring(0, element.text().length), offset);
			return prevOccurrences + occurrence;
		},

		// tested
		isInverseSelection : function(selection) {
			var isInDifferentElements = selection.anchorNode != selection.focusNode;
			if (!isInDifferentElements) {
				return selection.anchorOffset > selection.focusOffset;
			} else {
				return selection.anchorNode.compareDocumentPosition(selection.focusNode) == 2;
			}
		},
		
		updateIndices : function(selection) {
			var startNode = jQuery(selection.anchorNode);
			var endNode = jQuery(selection.focusNode);
			var startOffset = selection.anchorOffset;
			var endOffset = selection.focusOffset;
			if (this.isInverseSelection(selection)) {
				startNode = jQuery(selection.focusNode);
				endNode = jQuery(selection.anchorNode);
				startOffset = selection.focusOffset;
				endOffset = selection.anchorOffset;
			}
			var whitespaceRe = new RegExp(/\s+/g);
			var selectionString = selection.toString();
			if (whitespaceRe.test(selectionString.charAt(selectionString.length - 1))) {
				--this.endOffset;
			}
			var words = selectionString.trim().split(whitespaceRe);
			this.startIndex = this.getOccurrenceInElement(startNode, startOffset, words[0]);
			this.endIndex = this.startIndex;
			if (words.length > 1) {
				// TODO The following offset "fix" is dirty and will probably be broken for inverse selection
				var lastWord = words[words.length - 1];
				this.endIndex = this.getOccurrenceInElement(endNode, endOffset - lastWord.length, lastWord);
			}
			Tapestry.Logging.info("Start index: " + this.startIndex);
			Tapestry.Logging.info("End index: " + this.endIndex);
		},
		
		startSelection: function(target) {
			this.started = true;
			this.startId = target.attr("id");
			this.startIndex = null;
			this.endIndex = null;
			this.endId = null;
		},
		
		isBeingSelected: function() {
			if (!this.started ) return false;
			if(this.getSelection()) return true;
			return false;
		},
		
		stopSelection: function(target) {
			this.started = false;
			if(!this.getSelection()) {
				this.startId = null;
				this.endId = null;
				this.startIndex = null;
				this.endIndex = null;
				return;
			}
			this.endId = target.attr("id");
			this.updateIndices(this.getSelection());
		},

		/**
		 * Cross browser way to get selected text
		 * @return the selected text
		 */
		getSelection: function() {
		    if (window.getSelection) { 
			    return window.getSelection();
		    }else if (document.getSelection) {      
				return document.getSelection();
			}else { 
				return document.selection.createRange().text;
			}	
		}
}