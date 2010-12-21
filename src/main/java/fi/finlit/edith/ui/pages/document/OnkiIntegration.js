jQuery.noConflict();

var addConcept = function(uri, label){
	// TODO : what about multiple fields ?
	label = unescape(label);
	var conceptsField = jQuery("input[name='concepts']");
	var concepts = conceptsField.val();
	if (concepts.indexOf(uri) == -1) {
		// add to field
		if (concepts.length == 0){
			conceptsField.val([uri, ",", label].join(""));
		}else{
			conceptsField.val([concepts, ";", uri, ",", label].join(""));
		}
			
		// add to list
		var item = "<li about='" + uri + "'><span property='dc:title'>" + label + "</span><a href='#'>poista</a></li>"; 
		jQuery("#conceptsList").append(item);
	}	 
}

jQuery(document).ready(function() {

	jQuery("#conceptsList li a").live("click", function(){
		jQuery(this).parent().remove();	
		
		// recreate value
		var list = new Array();
		jQuery("#conceptsList li").each(function(){
			list.push(jQuery(this).attr("about") + "," + jQuery(this).children("span").html());
		});
		jQuery("input[name='concepts']").val(list.join(";"));
		
	});

});