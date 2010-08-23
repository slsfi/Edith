jQuery.noConflict();

var createNote = function() {
	if (!TextSelector.updateIndices(TextSelector.getSelection())) {
			alert(l10n.invalidSelection);
			return;
	}
	jQuery(":input[name='selectedStartId_1']").val(TextSelector.startId);
	jQuery(":input[name='selectedEndId_1']").val(TextSelector.endId);
	jQuery(":input[name='selectedText_1']").val(TextSelector.selection);
	jQuery(":input[name='selectedStartIndex_1']").val(TextSelector.startIndex);
	jQuery(":input[name='selectedEndIndex_1']").val(TextSelector.endIndex);

	//Submit form
	TapestryExt.submitZoneForm(jQuery("#createTerm").get(0));
	return false;
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

var updateUpdateLink = function() {
	if(TextSelector.getSelection() == "") { 
		jQuery("#longTextEditLink").hide();
	} else {
		jQuery("#longTextEditLink").show();
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
	
	jQuery("#mode_switcher").toggle(function() {
		disableLink = true;
		jQuery("#state").text("ANNOTOINTI");
	}, function() {
		disableLink = false;
		jQuery("#state").text("MUOKKAUS");
	});
	
    jQuery('.notecontent').live('click',
        function(event) {
            if (!disableLink) {
            	var classes = jQuery(this).attr('class').replace(/notecontent\ /g,'').replace(/\ /g, '/');
            	Editor.updateEditZone(classes);
            }
        }    
    );
    
    // live updated
    jQuery('.notelink').live('click',
    	function(event) {
    		var id = jQuery(this).attr('href').replace('#start','');
    		Editor.updateEditZone("n"+id);
    	}
    );
  
    jQuery('#createTermLink').bind('click', createNote);
    
    /* TODO disable for note editing!
    jQuery(document).keydown(function(event) {
    	if (event.which == 65) {
    		createNote();
    	}
    });
    */
    
    jQuery('#longTextEditLink').live('click', updateNote);

    jQuery('body').live('mousemove', updateSelectionLink);
    
    jQuery('body').live('mousemove', updateUpdateLink);
    
    jQuery("#note_filters input").click(function() {
    	toggleNoteListElements(jQuery(this));
    });
    
    jQuery("#dialog").jqm();
});

var Editor = {		
	updateEditZone: function(context){
		var link = editLink.replace('CONTEXT',context);
		TapestryExt.updateZone('editZone', link);
	},
}
