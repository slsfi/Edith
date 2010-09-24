jQuery.noConflict();

jQuery(function() {
	jQuery(".delete_dialog").hide();
	
	jQuery(".delete_question").click(function(event) {
		jQuery(this).siblings(".delete_dialog").show();
		jQuery(this).hide();
		event.preventDefault();
	});
	
	jQuery(".delete_decline").click(function(event) {
		jQuery(this).parent().siblings(".delete_question").show();
		jQuery(this).parent().hide();
		event.preventDefault();
	});
});