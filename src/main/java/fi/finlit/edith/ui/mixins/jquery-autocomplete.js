jQuery.noConflict();

Tapestry.Initializer.jQueryAutocompleter = function(elementId, url )
{
   // $T(elementId).autocompleter = new Ajax.Autocompleter(elementId, menuId, url, config);
	//alert("hello world " + elementId + ", " + url);
	
	jQuery("#" + elementId).autocomplete({
		source: url
		//source: ["c++", "java", "php", "coldfusion", "javascript", "asp", "ruby"]

	});
	
};