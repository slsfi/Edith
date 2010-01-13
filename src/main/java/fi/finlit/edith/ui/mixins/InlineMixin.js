
jQuery(document).ready(function(){
       jQuery(".editable").bind("keyup",
    		   function(event) {
    	   		  //Updating the hidden field with contents
    	   		  //of the editable element
    	   		   var elementId = 	jQuery(this).attr("id");
    	   		   var hiddenId = elementId.substring(12,elementId.length);
    	   		   
    	   		   jQuery("input[name='"+hiddenId+"']").val(jQuery(this).html());
    	   		   
    		   }
       );
     });

