jQuery.noConflict();

var Annotate = {
	
	selectedNoteId : null,
	
	updateSelectionForm : function() {
		//if (!TextSelector.updateIndices(TextSelector.getSelection())) {
		//	InfoMessage.showError("Could not make valid selection. Try again.");
		//	return false;
		//}
		jQuery(":input[name^='selectedStartId']").val(TextSelector.startId);
		jQuery(":input[name^='selectedEndId']").val(TextSelector.endId);
		jQuery(":input[name^='selectedText']").val(TextSelector.selection);
		jQuery(":input[name^='selectedStartIndex']").val(TextSelector.startIndex);
		jQuery(":input[name^='selectedEndIndex']").val(TextSelector.endIndex);
		return true;
	},

	createNote : function() {
		if(!this.updateSelectionForm() || jQuery('#createTermLink').hasClass('disabled')) {
			return false;
		}
		TapestryExt.submitZoneForm(jQuery("#createTermForm").get(0));
		return false;
	},

	connectNote : function() {
		if(!this.updateSelectionForm() || jQuery('#connectTermLink').hasClass('disabled')) {
			return false;
		}
		jQuery(":input[name='noteToLinkId']").val(this.selectedNoteId);
		TapestryExt.submitZoneForm(jQuery("#connectTermForm").get(0));
		return false;
	},
	
	setSelectedNote : function(noteId) {
		this.selectedNoteId = noteId;
	},
	
	updateSelectionLinks : function() {
		if( TextSelector.getSelection() == "" ) { 
			jQuery("#createTermLink").addClass('disabled');
			jQuery("#connectTermLink").addClass('disabled');
		} else if ( TextSelector.updateIndices(TextSelector.getSelection()) ) {
			jQuery("#createTermLink").removeClass('disabled');
			if (this.selectedNoteId !== null) {
				jQuery("#connectTermLink").removeClass('disabled');
			}
		}
	},
	
	clearSelectionLinks : function() {
		//Have fast version on the mousemove to clear out
		if( TextSelector.getSelection() == "" ) { 
			jQuery("#createTermLink").addClass('disabled');
			jQuery("#connectTermLink").addClass('disabled');
		}
	},
	
	freeResources : function() {
    	//Remove all CKEditor instances
  	  for(var i in CKEDITOR.instances) {
  		  try {
  			  CKEDITOR.instances[i].destroy();
  		  }catch(e) {
  			  alert("Got exception " + e);
  		  }
  		  // console.debug("Removed ckeditor instance");
  	  }
    },
	
	recreateCKEditors: function() {
	  if (Object.keys(window.CKEDITOR.instances).length === 0) {
	    jQuery(".note_edit .wysiwyg").ckeditor(Edith_CKEditorSetup);
      }
	}
	
};

var Edith_CKEditorSetup = {
	  removePlugins : "elementspath",
	  height: "40px",
	  skin: "kama",
	  entities: false,
	  extraPlugins: "autogrow",
	  autoGrow_minHeight: "40",
	  resize_enabled : false, 
	  startupFocus: false,
	  //resize_dir : "vertical",
	  toolbarCanCollapse : false,
	  //toolbarStartupExpanded: false,
	  toolbar : "edith",
	  toolbar_edith : [
      { name: 'basicstyles', items : [ 'SpecialChar', 'Bold','Italic','Underline','Subscript','Superscript','-','RemoveFormat' ] },
      { name: 'links', items : [ 'Link','Unlink' ] },
      { name: 'document', items : [ 'Source'] },
	  ],
};

jQuery(document).ready(function($) {
			
	$("body").bind(TapestryExt.EVT_BEFORE_ZONE_UPDATE, function() {
		//Remove ckeditor before ajax update
		Annotate.freeResources();
		//console.log("before zone update");
	});
	
	var disableLink = false;
	$("#normalNotes").removeAttr("href").addClass("disable_link");
	
	$("#normalNotes").click(function() {
		disableLink = false;
		$(this).removeAttr("href").addClass("disable_link");
		$("#innerNotes").attr("href", "#").removeClass("disable_link");
		
	});
	
	$("#innerNotes").click(function() {
		disableLink = true;
		$(this).removeAttr("href").addClass("disable_link");
		$("#normalNotes").attr("href", "#").removeClass("disable_link");
	});
	
	$('.noteanchor').live('click',
		function(event) {
        	if (!disableLink) {
        		var classes = "n"+$(this).attr('id').substring(3)
        		$(":input[name='selectedNoteId']").val(classes);
        		TapestryExt.submitZoneForm($("#selectNoteForm").get(0));
        	}
    	}    
	);			
	
    $('.noteanchor').live('mouseenter',
        function(event) {
    	    var classes = "n"+jQuery(this).attr('id').substring(3);
    	    jQuery("."+classes).addClass("underline");
        }
    );    
    
    $('.noteanchor').live('mouseleave',
        function(event) {
         	var classes = "n"+jQuery(this).attr('id').substring(3);
       	    jQuery("."+classes).removeClass("underline");
       }
    );    
  
    $('#createTermLink').bind('click', function() { Annotate.createNote() });
    $("#connectTermLink").bind("click", function() { Annotate.connectNote() });
    $('body').bind('mouseup', function() { Annotate.updateSelectionLinks() });
    $('body').bind('mousemove', function() { Annotate.clearSelectionLinks() });
    
    
    /* TODO disable for note editing!
    jQuery(document).keydown(function(event) {
    	if (event.which == 65) {
    		createNote();
    	}
    });
    */
   
    $(".jqmOpen").click(function() {
    	$("#dialog").jqm().jqmShow();
    });

    $(".jqmClose").click(function() {
    	$("#dialog").jqmHide();
    });
    
    
});
