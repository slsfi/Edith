jQuery(document).ready(function(){
    jQuery('.notecontent').bind('click',
        function(event) {
            var classes = jQuery(this).attr('class').replace('notecontent ','').replace(' ', '/');
            var link = editLink.replace('CONTEXT', classes);
            TapestryExt.activateZone('editZone', link);
        }    
    );
    
    jQuery('.notelink').bind('click',
    	function(event) {
    		var id = jQuery(this).attr('href').replace('#start','');
    		var link = editLink.replace('CONTEXT', "n"+id);
    		TapestryExt.activateZone('editZone', link);
    	}
    );		
});
        