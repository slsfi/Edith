
jQuery.noConflict();

Tapestry.Initializer.termAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
//		open: function(event, ui) {
//			alert(ui.item.basicForm);
//		},
		select: function(event, ui) { 
//			alert(ui.item.basicForm);
//			alert(ui.item.meaning);
			jQuery("input[]").text(ui.item.meaning);
			alert(ui.item.meaning);
			// TODO LIVE?
		}
		
	});
}

//var update_term_autocomplete = function(item) {
//	Tapestry.Logging.info("Got: " + item);
//}