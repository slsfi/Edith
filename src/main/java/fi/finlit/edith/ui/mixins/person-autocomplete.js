jQuery.noConflict();

Tapestry.Initializer.personAutocompleter = function(elementId) {
	jQuery("#" + elementId).autocomplete({
		select : function(event, ui) {
			/* FIXME The hackiest piece of code I've ever done. */
			var pageId = window.location.pathname.match(/annotate\/(\d+)/)[1];
			var path = window.location.pathname.replace(/annotate\/.+/, "annotate.noteedit.sksnoteform:person/" + ui.item.id + "?t:ac=" + pageId);
			TapestryExt.updateZone('personZone', path);
		}
	});
}
