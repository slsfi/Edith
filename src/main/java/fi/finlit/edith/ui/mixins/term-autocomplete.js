
jQuery.noConflict();

Tapestry.Initilizer.termAutocompleter = function(elmentId) {
	jQuery("#" + elementId).autocomplete({ source: url });
}

var update_term_autocomplete = function(item) {
	Tapestry.Logging.info("Got: " + item);
}