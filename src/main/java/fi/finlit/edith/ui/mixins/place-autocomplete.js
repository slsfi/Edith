jQuery.noConflict();

Tapestry.Initializer.placeAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select : function(event, ui) {
			TapestryExt.updateZone('placeZone', "/document/annotatepage.noteform:place/" + ui.item.id + "?t:ac=9&id=");
		}
	});
}
