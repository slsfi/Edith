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

jQuery(document).ready(function(){
    jQuery('.notecontent').live('click',
        function(event) {
            var classes = jQuery(this).attr('class').replace('notecontent ','').replace(' ', '/');
            Editor.updateEditZone(classes);
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
});

var Editor = {		
	updateEditZone: function(context){
		var link = editLink.replace('CONTEXT',context);
		TapestryExt.updateZone('editZone', link);
	},
	
}
