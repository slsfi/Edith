
jQuery(document).ready(function($){
       $(".editable").bind("keyup",
    		   function(event) {
    	   		  //Updating the hidden field with contents
    	   		  //of the editable element
    	   		   var elementId = 	$(this).attr("id");
    	   		   var hiddenId = elementId.substring(12,elementId.length);
    	   		   var hiddenEl  = $("input[name='"+hiddenId+"']");
    	   		   
    	   		   hiddenEl.val($(this).html());
    	   		   
    	   		   $(this).addClass("edited");
    		   }
       );
     });

