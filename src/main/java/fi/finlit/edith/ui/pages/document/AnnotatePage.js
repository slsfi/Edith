jQuery.noConflict();

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
    
    jQuery('#createTermLink').bind('click',
    	function(event) {
    		if (!TextSelector.startId) {
    			Tapestry.Logging.info("missing startId");
    			return false;
    		}
			if (!TextSelector.endId) {
				Tapestry.Logging.info("missing endId");
				return false;
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
    );
    
    jQuery('#longTextEditLink').live('click',
        	function(event) {
			    if (!TextSelector.startId) {
					Tapestry.Logging.info("missing startId");
					return false;
				}
				if (!TextSelector.endId) {
					Tapestry.Logging.info("missing endId");
					return false;
				}
				jQuery("#longTextEdit").html(TextSelector.getSelection());
	        	jQuery("#longTextEdit").addClass("edited");
	    		jQuery(":input[name='selectedStartId_2']").val(TextSelector.startId);
	    		jQuery(":input[name='selectedEndId_2']").val(TextSelector.endId);
	    		jQuery(":input[name='selectedText_2']").val(TextSelector.selection);
	    		jQuery(":input[name='selectedStartIndex_2']").val(TextSelector.startIndex);
	    		jQuery(":input[name='selectedEndIndex_2']").val(TextSelector.endIndex);
	    		return false;
        	}
    );
          
    jQuery('.tei').live('mouseup',
    	function(event) {
    		TextSelector.stopSelection(jQuery(event.target));
    		if(!TextSelector.getSelection()) {
    			jQuery(".selection-link").addClass('disabled');
    		}
    	}
    );
    jQuery('.tei').live('mousedown',
        	function(event) {
        		TextSelector.startSelection(jQuery(event.target));
        		jQuery(".selection-link").addClass('disabled');
        		jQuery("#longTextEditLink").hide();
        	}
        );
    jQuery('.tei').live('mousemove',
        	function(event) {
        		if(!TextSelector.isBeingSelected()) return;
        		jQuery(".selection-link").removeClass('disabled');
        		jQuery("#longTextEditLink").show();
        	}
        );
});

var Editor = {		
	updateEditZone: function(context){
		var link = editLink.replace('CONTEXT',context);
		TapestryExt.updateZone('editZone', link);
	},
	
}

