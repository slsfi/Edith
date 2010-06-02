
jQuery.noConflict();

Tapestry.Initializer.nameFormAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select: function(event, ui) {
			jQuery("input[name='normalizedName']").attr("value", ui.item.normalizedName);
			jQuery("input[name='normalizedDescription']").attr("value", ui.item.normalizedDescription);
			event.preventDefault();
		}
	});
}
