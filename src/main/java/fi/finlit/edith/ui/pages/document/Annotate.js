jQuery.noConflict();

var Annotate = {
	
	selectedNoteId : null,
	
	updateSelectionForm : function() {
		if (!TextSelector.updateIndices(TextSelector.getSelection())) {
			InfoMessage.showError("Could not make valid selection. Try again.");
			return false;
		}
		jQuery(":input[name^='selectedStartId']").val(TextSelector.startId);
		jQuery(":input[name^='selectedEndId']").val(TextSelector.endId);
		jQuery(":input[name^='selectedText']").val(TextSelector.selection);
		jQuery(":input[name^='selectedStartIndex']").val(TextSelector.startIndex);
		jQuery(":input[name^='selectedEndIndex']").val(TextSelector.endIndex);
		return true;
	},

	createNote : function() {
		if(!this.updateSelectionForm()) {
			return false;
		}
		TapestryExt.submitZoneForm(jQuery("#createTermForm").get(0));
		return false;
	},

	connectNote : function() {
		if(!this.updateSelectionForm()) {
			return false;
		}
		jQuery(":input[name='noteToLinkId']").val(Annotate.selectedNoteId);
		TapestryExt.submitZoneForm(jQuery("#connectTermForm").get(0));
		return false;
	},
	
	setSelectedNote : function(noteId) {
		this.selectedNoteId = noteId;
	},
	
	updateSelectionLinks : function() {
		if( TextSelector.getSelection() == "" ) { 
			jQuery("#createTermLink").addClass('disabled');
			jQuery("#connectTermLink").addClass('disabled');
		} else {
			jQuery("#createTermLink").removeClass('disabled');
			if (this.selectedNoteId !== null) {
				jQuery("#connectTermLink").removeClass('disabled');
			}
		}
	},

};

jQuery(document).ready(function() {
	var disableLink = false;
	jQuery("#normalNotes").removeAttr("href").addClass("disable_link");
	
	jQuery("#normalNotes").click(function() {
		disableLink = false;
		jQuery(this).removeAttr("href").addClass("disable_link");
		jQuery("#innerNotes").attr("href", "#").removeClass("disable_link");
		
	});
	
	jQuery("#innerNotes").click(function() {
		disableLink = true;
		jQuery(this).removeAttr("href").addClass("disable_link");
		jQuery("#normalNotes").attr("href", "#").removeClass("disable_link");
	});
	
    jQuery('.notecontent').live('click',
        function(event) {
            if (!disableLink) {
            	var classes = jQuery(this).attr('class').replace(/notecontent\ /g,'').replace(/\ /g, '/');
            	jQuery(":input[name='selectedNoteLocalId']").val(classes);
    			TapestryExt.submitZoneForm(jQuery("#selectNoteForm").get(0));
            }
        }    
    );
    
    //jQuery(".selectable-note").live('click',
    //	function(event) {
    //		alert("selected");
    //	}
    //);
    
    // live updated
    //jQuery('.notelink').live('click',
    //	function(event) {
    //		var localId= jQuery(this).attr('href').replace('#start','');
	//		var noteId = jQuery(this).attr('id').replace('noteid', '');		
    //		//Editor.updateEditZone("e" + localId + "/" + noteId);
    //	}
    //);
  
    jQuery('#createTermLink').bind('click', function() { Annotate.createNote() });
    jQuery("#connectTermLink").bind("click", function() { Annotate.connectNote() });
    jQuery('body').bind('mousemove', function() { Annotate.updateSelectionLinks() });
    
    /* TODO disable for note editing!
    jQuery(document).keydown(function(event) {
    	if (event.which == 65) {
    		createNote();
    	}
    });
    */
   
    jQuery(".jqmOpen").click(function() {
    	jQuery("#dialogZone").text("Hetki...");
    	jQuery("#dialog").jqm().jqmShow();
    });

    jQuery(".jqmClose").click(function() {
    	jQuery("#dialog").jqmHide();
    });
    
});
