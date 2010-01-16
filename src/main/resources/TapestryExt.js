/**
 * Commonly needed functions that are absent in the default Tapestry js lib.
 */
var TapestryExt = {
  /**
   * If the specified form listens for the Tapestry.FORM_PROCESS_SUBMIT_EVENT event
   * (all forms with a zone specified do), it will AJAX-submit and update its zone after.
   */
  submitZoneForm : function(element) {    
    element = $(element)
    if (!element) {
      Tapestry.error('Could not find form to trigger AJAX submit on');
      return;
    }
    element.fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
  },
  
  activateZone : function( zoneId, url ) {
	  var zoneManager = Tapestry.findZoneManagerForZone( zoneId );
	  if (zoneManager){
		  zoneManager.updateFromURL( url );  
	  }	  	  
  },
  
  /**
   * Trigger any link, zone or not.
   */
  triggerLink : function(element, url) {
    element = $(element);
    var zoneManager = Tapestry.findZoneManager(element);
    if (zoneManager) {    
        zoneManager.updateFromURL(url);
    }else {
        window.location.href = url;
    }
  }  
}