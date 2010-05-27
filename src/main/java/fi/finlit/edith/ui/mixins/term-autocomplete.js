
jQuery.noConflict();

Tapestry.Initializer.termAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select: function(event, ui) { 
			jQuery("textarea[name='meaning']").attr("value", ui.item.meaning);
			jQuery("select[name='language']").attr("value", ui.item.language);
		}
	});
}
