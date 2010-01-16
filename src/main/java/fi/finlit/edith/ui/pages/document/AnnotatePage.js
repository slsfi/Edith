jQuery(document).ready(function(){
    jQuery('.notecontent').bind('click',
        function(event) {
            var classes = jQuery(this).attr('class').replace('notecontent ','').replace(' ', '/');
            var link = editLink.replace('CONTEXT', classes);
            TapestryExt.activateZone('editZone', link);
        }    
    );
});