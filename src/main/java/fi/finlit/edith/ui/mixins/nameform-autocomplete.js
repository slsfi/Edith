
jQuery.noConflict();

Tapestry.Initializer.nameFormAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select: function(event, ui) {
			alert("jeejee");
			event.preventDefault();
		}
	});
}
