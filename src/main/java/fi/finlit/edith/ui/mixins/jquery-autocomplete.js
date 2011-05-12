jQuery.noConflict();

Tapestry.Initializer.jQueryAutocompleter = function(config)
{
	jQuery("#" + config.elementId).autocomplete({ source: config.url });
};