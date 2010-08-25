jQuery.noConflict();

Tapestry.Initializer.personAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select : function(event, ui) {
			TapestryExt.updateZone('personZone', "/document/annotatepage.noteform:person/" + ui.item.id + "?t:ac=9&id=");
		}
	});
}
