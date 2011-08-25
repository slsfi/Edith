var SLSNoteForm = {
    toggleOtherLanguage : function() {
      if (jQuery("select[name='language']").val() === "OTHER") {
          jQuery(":input[name='otherLanguage']").show();
      } else {
          jQuery(":input[name='otherLanguage']").hide();
      }
    },
    triggerNextActionLink : function(element) {
      var actionLink = jQuery(element).next('span').find('a').click();
      Tapestry.findZoneManager(actionLink.get(0)).updateFromURL(actionLink.attr("href"));
    }
};