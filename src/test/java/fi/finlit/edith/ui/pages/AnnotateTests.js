var findOccurance = function(str, substr, index) {
	var occurance = 0;
	for ( var i = 0; i < str.length; ++i) {
		var currentText = str.substring(i);
		if (currentText.startsWith(substr)) {
			++occurance;
			if (i == index) {
				return occurance;
			}
		}
	}
	return null;
}

var isInverseSelection = function() {
	var selection = window.getSelection();
	// TODO Parent node check is not enough, we need to get ancestor with ID (jQuery)
	var isInDifferentElements = selection.anchorNode.parentNode != selection.focusNode.parentNode;
	if (!isInDifferentElements) {
		return selection.anchorOffset > selection.focusOffset;
	} else {
		// TODO Compare ancestors in document
		return false
	}
}

jQuery(document).ready(function() {
		// Basic case
		jQuery("button").click(function() {
			var selection = window.getSelection();
			var anchorNode = jQuery(selection.anchorNode);
			var focusNode = jQuery(selection.focusNode);
			var anchorOffset = selection.anchorOffset;
			var focusOffset = selection.focusOffset;
			var offset = null;
			var text = anchorNode.parent().text();
			
			/* TODO 
			 * Detect start and end element
			 * 
			 * If start != end and first word != last word
			 * 		Find occurrences in previous siblings for start and end
			 * Else
			 * 		Handle only start
			 * 
			 * Start index = occurrence of selection's first word in start element
			 * End index = occurrence of selections's last word in end element
			 */

			// TODO anchorNode switch focusNode and anchorOffset switch focusOffset if inverseSelection
			offset = isInverseSelection() ? focusOffset : anchorOffset; 
			
			// This works only for single element selections
			var re = new RegExp(/\s/g);
			text = text.replace(re, " ");
			selection = selection.toString().replace(re, " ");
			// Increment offset when character is whitespace
			for (var i = offset; i < text.length; ++i) {
				if (re.test(text.charAt(i))) {
					++offset
				} else {
					break;
				}
			}
			var occurance = findOccurance(text, selection, offset)
			Tapestry.Logging.info("Occurance: " + occurance);
	});
});