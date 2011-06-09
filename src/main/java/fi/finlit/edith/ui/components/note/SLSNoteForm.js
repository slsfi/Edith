
var SLSNoteForm = {
	toggleOtherLanguage : function() {
      if (jQuery("select[name='language']").val() === "OTHER") {
          jQuery(":input[name='otherLanguage']").show();
      } else {
          jQuery(":input[name='otherLanguage']").hide();
      }
    }
    
};