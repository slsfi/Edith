jQuery.noConflict();

Tapestry.Initializer.jQueryAutocompleter = function(elementId, url )
{
	jQuery("#" + elementId).autocomplete({ source: url });
};