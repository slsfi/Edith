jQuery.noConflict();

Tapestry.Initializer.termAutocompleter = function(elementId) {
  jQuery("#" + elementId).autocomplete({
    select : function(event, ui) {
      /* FIXME The hackiest piece of code I've ever done. And now I even copypasted it, twice! */
      var pageId = window.location.pathname.match(/annotate\/(\d+)/)[1];
      var path = window.location.pathname.replace(/annotate\/.+/, "annotate.noteedit.sksnoteform:term/" + ui.item.id + "?t:ac=" + pageId);
      TapestryExt.updateZone('termZone', path);
    }
  });
}
