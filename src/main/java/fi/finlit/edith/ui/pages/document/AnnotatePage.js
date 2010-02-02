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
    		if (!TextSelector.startId || !TextSelector.endId) {
    			Tapestry.Logging.info("no selection");
    			Tapestry.Logging.info("startId " + TextSelector.startId);
    			Tapestry.Logging.info("endId " + TextSelector.endId);
    			return false;
    		}
    		
    		jQuery(":input[name='selectedStartId_1']").val(TextSelector.startId);
    		jQuery(":input[name='selectedEndId_1']").val(TextSelector.endId);
    		jQuery(":input[name='selectedText_1']").val(TextSelector.getSelection());
    	
    		//Submit form
    		TapestryExt.submitZoneForm(jQuery("#createTerm").get(0));
    		return false;
    	}
    );
    
    jQuery('#longTextEditLink').live('click',
        	function(event) {
	        	jQuery("#longTextEdit").html(TextSelector.getSelection());
	        	jQuery("#longTextEdit").addClass("edited");
	    		jQuery(":input[name='selectedStartId_2']").val(TextSelector.startId);
	    		jQuery(":input[name='selectedEndId_2']").val(TextSelector.endId);
	    		jQuery(":input[name='selectedText_2']").val(TextSelector.getSelection());
	    		return false;
        	}
    );
      
    var findContainer = function(element) {
		return jQuery(element).parents(".sp,.stage").eq(0);
	}
    
    jQuery('.act').live('mouseup',
    	function(event) {
    		TextSelector.stopSelection(findContainer(event.target));
    		if(!TextSelector.getSelection()) {
    			jQuery(".selection-link").addClass('disabled');
    		}
    	}
    );
    jQuery('.act').live('mousedown',
        	function(event) {
        		TextSelector.startSelection(findContainer(event.target));
        		jQuery(".selection-link").addClass('disabled');
        		jQuery("#longTextEditLink").hide();
        	}
        );
    jQuery('.act').live('mousemove',
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

var TextSelector = {
		started : false,
		startId : null,
		endId : null,
		
		startSelection: function(target) {
			this.started = true;
			this.startId = target.attr("id");
			this.endId = null;
		},
		
		isBeingSelected: function() {
			if (!this.started ) return false;
			if(this.getSelection()) return true;
			return false;
		},
		
		stopSelection: function(target) {
			this.started = false;
			if(!this.getSelection()) {
				this.startId = null;
				this.endId = null;
				return;
			}
			this.endId = target.attr("id");
			
			//Tapestry.Logging.info("startId " + this.startId);
			//Tapestry.Logging.info("endId " + this.endId);
			//Tapestry.Logging.info("selected text " + this.getSelection());
		},

		/**
		 * Cross browser way to get selected text
		 * @return the selected text
		 */
		getSelection: function() {
			if (document.getSelection) {
				return document.getSelection();
			}
			else if (window.getSelection) {
				return window.getSelection();
			}else {
				return document.selection.createRange().text;
			}	
		}
}