jQuery.noConflict();

Tapestry.Initializer.termAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select: function(event, ui) {
			jQuery("input[name='basicForm']").attr("value", ui.item.basicForm);
			jQuery("textarea[name='termMeaning']").attr("value", ui.item.meaning);
			jQuery("select[name='language']").attr("value", ui.item.language);
			event.preventDefault();
		}
	});
}
