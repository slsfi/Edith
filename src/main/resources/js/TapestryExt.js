/**
 * Commonly needed functions that are absent in the default Tapestry js lib.
 */
var TapestryExt = {
		
   
   EVT_BEFORE_ZONE_UPDATE : "event.before_zone_update",
   beforeUpdateEvent : function() {
	   jQuery("body").trigger(this.EVT_BEFORE_ZONE_UPDATE);
   },
		
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
    this.beforeUpdateEvent();
    element.fire(Tapestry.FORM_PROCESS_SUBMIT_EVENT);
  },
  
  updateZone : function( zoneId, url ) {
	  var zoneManager = Tapestry.findZoneManagerForZone( zoneId );
	  if (zoneManager){
		  this.beforeUpdateEvent();
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
    	this.beforeUpdateEvent();
        zoneManager.updateFromURL(url);
    }else {
    	this.beforeUpdateEvent();
        window.location.href = url;
    }
  }  
}