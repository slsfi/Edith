jQuery(document).ready(function() {
	var $ = jQuery;
	$("button").click(function() {
		var selection = window.getSelection();
		var anchorNode = jQuery(selection.anchorNode);
		var text = anchorNode.parent().text();
		var offset = selection.anchorOffset < selection.focusOffset ? selection.anchorOffset
				: selection.focusOffset;
		var re = new RegExp(/\s/g);
		text = text.replace(re, " ");
		selection = selection.toString().replace(re, " ");
		for (var i = offset; i < text.length; ++i) {
			if (re.test(text.charAt(i))) {
				++offset
			} else {
				break;
			}
		}
		var occurances = 0;
		var occurance = null;
		for (var i = 0; i < text.length; ++i) {
			var currentText = text.substring(i);
			if (currentText.startsWith(selection)) {
				++occurances;
				if (i == offset) {
					occurance = occurances;
				}
			}
		}
		Tapestry.Logging.info("Occurance: " + occurance);
	});
});