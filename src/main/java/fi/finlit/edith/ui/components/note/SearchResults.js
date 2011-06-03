          
var SearchResults = {
   toggleNoteListElements : function(checkbox) {
        var checkbox = checkbox;
        var isChecked = checkbox.attr("checked");
        if (isChecked) {
            jQuery(".notes ." + checkbox.attr("name")).show();
        } else {
            jQuery(".notes ." + checkbox.attr("name")).hide();
        }
    }
};

jQuery(function() {
	jQuery("#note_filters input").click(function() {
        SearchResults.toggleNoteListElements(jQuery(this));
    });
    
    jQuery(".search-results .selectable-note").live("click",
      function(event) {
    	  jQuery(".search-results .notes li").removeClass("selected-note");
          jQuery(this).closest("li").addClass("selected-note");
          
          //Update note instances view on the selection
          TapestryExt.updateZone('documentNotesZone', jQuery(this).attr("href"));
          return false;
      }
    );
});
