jQuery(document).ready(function(){
    jQuery('.notecontent').bind('click',
        function(event) {
            var classes = jQuery(this).attr('class').replace('notecontent ','').replace(' ', '/');
            Editor.updateEditZone(classes);
        }    
    );
    
    jQuery('.notelink').bind('click',
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
    		
    		jQuery(":input[name='selectedStartId']").val(TextSelector.startId);
    		jQuery(":input[name='selectedEndId']").val(TextSelector.endId);
    		jQuery(":input[name='selectedText']").val(TextSelector.getSelection());
    	
    		//Submit form
    		jQuery("#createTerm").submit();
    		return false;
    	}
    );
    
    var findContainer = function(element) {
		return jQuery(element).parents(".sp,.stage").eq(0);
	}
    
    jQuery('.act').bind('mouseup',
    	function(event) {
    		TextSelector.stopSelection(findContainer(event.target));
    		if(!TextSelector.getSelection())
    			jQuery("#createTermLink").addClass('disabled');
    	}
    );
    jQuery('.act').bind('mousedown',
        	function(event) {
        		TextSelector.startSelection(findContainer(event.target));
        		jQuery("#createTermLink").addClass('disabled');
        	}
        );
    jQuery('.act').bind('mousemove',
        	function(event) {
        		if(!TextSelector.isBeingSelected()) return;
        		jQuery("#createTermLink").removeClass('disabled');
        	}
        );
});

var Editor = {		
	updateEditZone: function(context){
		var link = editLink.replace('CONTEXT',context);
		TapestryExt.updateZone('editZone', link);
	}

	
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