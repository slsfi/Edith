jQuery.noConflict();

// tested
var getOccurrenceInString = function(str, substr, minIndex) {	
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
}

// tested
var getOccurrences = function(str, substr) {
	var occurrence = 0;
	for (var i = 0; i < str.length; ++i) {
		if (str.substring(i).indexOf(substr) == 0) {
			++occurrence;
		}
	}
	return occurrence;
}

// tested
var prevAllString = function(element) {
	var text = new Array();
	element = element.previousSibling;
	while (element != null) {
		text.push(element.nodeValue != null ? element.nodeValue : element.innerHTML);
		element = element.previousSibling;
	}
	text.reverse();
	return text.join("");			
}

// tested
var getOccurrenceInElement = function(element, offset, substr) {
	var prevOccurrences = getOccurrences(prevAllString(element.get(0)), substr);
	if (element.parent().hasClass("notecontent")){
		prevOccurrences += getOccurrences(prevAllString(element.parent().get(0)), substr);
	}
	var occurrence = getOccurrenceInString(element.text(), substr.substring(0, element.text().length), offset);
	return prevOccurrences + occurrence;
}

// tested
var isInverseSelection = function(selection) {
	var isInDifferentElements = selection.anchorNode != selection.focusNode;
	if (!isInDifferentElements) {
		return selection.anchorOffset > selection.focusOffset;
	} else {
		return selection.anchorNode.compareDocumentPosition(selection.focusNode) == 2;
	}
}

jQuery(document).ready(function() {
		// Basic case
		jQuery("button").click(function() {
			var selection = window.getSelection();
			var startNode = jQuery(selection.anchorNode);
			var endNode = jQuery(selection.focusNode);
			var startOffset = selection.anchorOffset;
			var endOffset = selection.focusOffset;
			
			if (isInverseSelection(selection)) {
				startNode = jQuery(selection.focusNode);
				endNode = jQuery(selection.anchorNode);
				startOffset = selection.focusOffset;
				endOffset = selection.anchorOffset;
			}
			
			var whitespaceRe = new RegExp(/\s+/g);
			var selectionString = selection.toString();
			if (selectionString.charAt(selectionString.length - 1) == " ") {
				--endOffset;
			}
			var words = selectionString.trim().split(whitespaceRe);
			var startIndex = getOccurrenceInElement(startNode, startOffset, words[0]);
			var endIndex = startIndex;
			if (words.length > 1) {
				// TODO The following offset "fix" is dirty and will probably be broken for inverse selection
				var lastWord = words[words.length - 1];
				endIndex = getOccurrenceInElement(endNode, endOffset - lastWord.length, lastWord);
			}
			Tapestry.Logging.info("Start index: " + startIndex);
			Tapestry.Logging.info("End index: " + endIndex);
	});
});