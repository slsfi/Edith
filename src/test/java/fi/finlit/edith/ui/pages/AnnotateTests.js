jQuery.noConflict();

//var whitespaceRe = new RegExp(/\s/g); // TODO Global?
//str = str.replace(whitespaceRe, " ");
//substr = substr.replace(whitespaceRe, " ");
//// Increment offset when character is whitespace
//for (var i = index; i < str.length; ++i) {
//	if (whitespaceRe.test(str.charAt(i))) {
//		++index
//	} else {
//		break;
//	}
//}

// tested
var getOccurrenceInString = function(str, substr, minIndex) {	
	var occurrence = 0;
	for (var i = 0; i < str.length; ++i) {
		if (str.substring(i).indexOf(substr) == 0) {
			++occurrence;
			if (i >= minIndex) { // TODO Verify if this works for whitespace + added '='
				return occurrence;
			}
		}
	}
	return 0;
}

// tested
var getOccurrences = function(str, substr) {
	var occurrence = 0;
	for ( var i = 0; i < str.length; ++i) {
		if (str.substring(i).indexOf(substr) == 0) {
			++occurrence;
		}
	}
	return occurrence;
}

var getOccurrenceInElement = function(element, offset, substr) {
	// TODO Handle also selections with already annotated sections	
	var ancestor = element.parent();
	var prevOccurrences = getOccurrences(element.prevAll().text(), substr);
	var occurrence = getOccurrenceInString(element.text(), substr, offset);
	return prevOccurrences + occurrence;
}

var isInverseSelection = function() {
	var selection = window.getSelection();
	// TODO Parent node check is not enough, we need to get ancestor with ID (jQuery)
	var isInDifferentElements = selection.anchorNode.parentNode != selection.focusNode.parentNode;
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
			
			if (isInverseSelection()) {
				startNode = jQuery(selection.focusNode);
				endNode = jQuery(selection.anchorNode);
				startOffset = selection.focusOffset;
				endOffset = selection.anchorOffset;
			}
			
			var whitespaceRe = new RegExp(/\s/g);
			// TODO Splitting strings like "foo  bar" returns ["foo", "", "bar"] which is not very nice I guess
			var words = selection.toString().trim().split(whitespaceRe);
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