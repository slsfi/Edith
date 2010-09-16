jQuery.noConflict();

Tapestry.Initializer.placeAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select : function(event, ui) {
		/* FIXME The hackiest piece of code I've ever done. And now I even copypasted it. */
		var pageId = window.location.pathname.match(/annotate\/(\d+)/)[1];
		var path = window.location.pathname.replace(/annotate\/.+/, "annotatepage.noteform:place/" + ui.item.id + "?t:ac=" + pageId);
		TapestryExt.updateZone('placeZone', path);
		}
	});
}
