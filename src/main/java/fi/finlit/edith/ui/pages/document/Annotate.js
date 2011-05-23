jQuery.noConflict();

var createNote = function() {
	if (!TextSelector.updateIndices(TextSelector.getSelection())) {
			alert(l10n.invalidSelection);
			return false;
	}
	jQuery(":input[name='selectedStartId_1']").val(TextSelector.startId);
	jQuery(":input[name='selectedEndId_1']").val(TextSelector.endId);
	jQuery(":input[name='selectedText_1']").val(TextSelector.selection);
	jQuery(":input[name='selectedStartIndex_1']").val(TextSelector.startIndex);
	jQuery(":input[name='selectedEndIndex_1']").val(TextSelector.endIndex);

	//Submit form
	TapestryExt.submitZoneForm(jQuery("#createTerm").get(0));
	return true;
}

var updateNote = function() {
	if (!TextSelector.updateIndices(TextSelector.getSelection())) {
		alert(l10n.invalidSelection);
		return;
	}
	
	jQuery("#longTextEdit").html(TextSelector.selection);
	jQuery(":input[name='selectedStartId_2']").val(TextSelector.startId);
	jQuery(":input[name='selectedEndId_2']").val(TextSelector.endId);
	jQuery(":input[name='selectedText_2']").val(TextSelector.selection);
	jQuery(":input[name='selectedStartIndex_2']").val(TextSelector.startIndex);
	jQuery(":input[name='selectedEndIndex_2']").val(TextSelector.endIndex);
	return false;
}

var updateSelectionLink = function() {
	if(TextSelector.getSelection() == "") { 
		jQuery(".selection-link").addClass('disabled');
	} else {
		jQuery(".selection-link").removeClass('disabled');
	}
}

var toggleNoteListElements = function(checkbox) {
	var checkbox = checkbox;
	var isChecked = checkbox.attr("checked");
	if (isChecked) {
		jQuery(".notes ." + checkbox.attr("name")).show();
	} else {
		jQuery(".notes ." + checkbox.attr("name")).hide();
	}
}

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
    
    jQuery(".selectable-note").live('click',
    	function(event) {
    		alert("selected");
    	}
    );
    
    // live updated
    jQuery('.notelink').live('click',
    	function(event) {
    		var localId= jQuery(this).attr('href').replace('#start','');
			var noteId = jQuery(this).attr('id').replace('noteid', '');
			var url = ".documentnotes.selectdocumentnote/" + noteId
			TapestryExt.updateZone("documentNotesZone", url);
			
    		//Editor.updateEditZone("e" + localId + "/" + noteId);
    	}
    );
  
    jQuery('#createTermLink').bind('click', function() {
    	//jQuery("#dialogZone").text("Odota hetki!");
    	//if (createNote()) {
    		//jQuery("#dialog").jqm().jqmShow();
    	//}
    	createNote();
    });
    
    /* TODO disable for note editing!
    jQuery(document).keydown(function(event) {
    	if (event.which == 65) {
    		createNote();
    	}
    });
    */
    
    jQuery('#longTextEditLink').live('click', updateNote);

    jQuery('body').live('mousemove', updateSelectionLink);
    
    jQuery("#note_filters input").click(function() {
    	toggleNoteListElements(jQuery(this));
    });
    
    jQuery(".jqmOpen").click(function() {
    	jQuery("#dialogZone").text("Hetki...");
    	jQuery("#dialog").jqm().jqmShow();
    });

    jQuery(".jqmClose").click(function() {
    	jQuery("#dialog").jqmHide();
    });
    
});

var Editor = {		
	updateEditZone: function(context){
		alert("updateEditZone: " + context);
		var link = editLink.replace('CONTEXT',context);
		TapestryExt.updateZone('editZone', link);
	},
}
