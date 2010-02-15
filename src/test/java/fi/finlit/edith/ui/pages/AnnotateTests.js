jQuery.noConflict();

var getOccurrenceInString = function(str, substr, index) {
//	var whitespaceRe = new RegExp(/\s/g); // TODO Global?
//	str = str.replace(whitespaceRe, " ");
//	substr = substr.replace(whitespaceRe, " ");
//	// Increment offset when character is whitespace
//	for (var i = index; i < str.length; ++i) {
//		if (whitespaceRe.test(str.charAt(i))) {
//			++index
//		} else {
//			break;
//		}
//	}
	
	var occurrence = 0;
	for (var i = 0; i < str.length; ++i) {
		var currentText = str.substring(i);
		if (currentText.startsWith(substr)) {
			++occurrence;
			if (i >= index) { // TODO Verify if this works for whitespace + added '='
				return occurrence;
			}
		}
	}
	return null;
}

var getOccurrences = function(str, substr) {
	var occurrence = 0;
	for ( var i = 0; i < str.length; ++i) {
		var currentText = str.substring(i);
		if (currentText.startsWith(substr)) {
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
		// TODO Compare ancestors in document
		// return true if inverse else false
		
		var startNode = jQuery(selection.anchorNode);
		var endNode = jQuery(selection.focusNode);
		return
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
			var words = selection.toString().split(whitespaceRe);
			var startIndex = getOccurrenceInElement(startNode, startOffset, words[0]);
			var endIndex = startIndex;
			if (words.length > 1) {
				// TODO The following offset "fix" is dirty and will be broken for inverse selection
				endIndex = getOccurrenceInElement(endNode, endOffset - words[words.length - 1].length, words[words.length - 1]);
			}
			Tapestry.Logging.info("Start index: " + startIndex);
			Tapestry.Logging.info("End index: " + endIndex);
	});
});