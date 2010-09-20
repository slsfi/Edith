jQuery.noConflict();

Tapestry.Initializer.termAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select: function(event, ui) {
			jQuery("input[name='termId']").attr("value", ui.item.id);
			jQuery("input[name='basicForm']").attr("value", ui.item.basicForm);
			var termMeaning = jQuery("textarea[name='termMeaning']");
			if (ui.item.meaning == undefined) {
				termMeaning.attr("value", "");
			} else {
				termMeaning.attr("value", ui.item.meaning);
			}
			jQuery("select[name='language']").attr("value", ui.item.language);
			event.preventDefault();
		}
	});
}
