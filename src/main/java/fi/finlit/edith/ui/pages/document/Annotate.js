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
		if(!this.updateSelectionForm()) {
			return false;
		}
		TapestryExt.submitZoneForm(jQuery("#createTermForm").get(0));
		return false;
	},

	connectNote : function() {
		if(!this.updateSelectionForm()) {
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
  		  CKEDITOR.instances[i].destroy();
  		  //console.debug("Removed ckeditor instance");
  	  }
    }
	
};

/*
var Autoresize = {
		resizeMinHeight : 30,
		
		diff : 20,
		
		body : function(editor_id) { return jQuery("#"+editor_id+"_ifr").contents().find("body") },
		ifr : function(editor_id) { return jQuery("#"+editor_id+"_ifr") },
		tbl : function(editor_id) { return jQuery("#"+editor_id+"_tbl") },
		toolbar : function(editor_id) { return jQuery("#" + editor_id + "_tbl .mceToolbar") },
		
		initialResize : function(editor_id, h) {
			this.resizeEditor(editor_id, h);
			this.body(editor_id).css("overflow","hidden");
			this.resizeMinHeight = this.ifr(editor_id).height() - this.diff;
			console.log("min height is " + this.resizeMinHeight);
		},
		
		resizeEditor : function(editor_id, h) {
	    	this.tbl(editor_id).height(h); 
	    	this.ifr(editor_id).height(h);
	    	var cH = this.ifr(editor_id).height();
	    	//jQuery("#"+editor_id+"_ifr").contents().find("body").height(cH - this.diff);
	    	console.log("set height of the " + editor_id + " to " + h);
		},
		
		recalculate: function(editor_id) {
			var editHeight = this.body(editor_id).height();
			var contHeight = this.ifr(editor_id).height() - this.diff;
			console.log("recalculate " + editor_id +" edit height = " + editHeight + " cont height " + contHeight);
			if (editHeight != contHeight) {
				if (editHeight < this.resizeMinHeight) {
					editHeight = this.resizeMinHeight;
				}
				//this.body(editor_id).scrollTo(0);
				this.resizeEditor(editor_id, (editHeight+this.diff)+"px");
			}
		}		
};
*/

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

jQuery(document).ready(function() {
	
	/*
	
	tinymce.init({
	    //script_url : '/js/tiny_mce/tiny_mce.js',
		mode: "none",
	    theme : "advanced",
	    skin : "default",
	    width: "95%",
	    setup: function(ed) {
	    	ed.onPostRender.add(function(ed, cm) {
	    		Autoresize.initialResize(ed.id, "2em");
	    		//Hide all toolbars
				jQuery(".mceToolbar").hide();
	            console.log('After render: ' + ed.id);
	        });
	    	ed.onSetContent.add(function(ed) { Autoresize.recalculate(ed.id) });
	    	ed.onKeyUp.add(function(ed){ Autoresize.recalculate(ed.id); Autoresize.toolbar(ed.id).show(); });
	    	ed.onActivate.add(function(ed) { Autoresize.recalculate(ed.id); Autoresize.toolbar(ed.id).show() });
	    	ed.onDeactivate.add(function(ed) { Autoresize.toolbar(ed.id).hide(); });
	    	ed.onClick.add(function(ed) { Autoresize.recalculate(ed.id); Autoresize.toolbar(ed.id).show() });
	    },
	    //plugins: "autoresize",
	    plugins : "inlinepopups, tabfocus",
	    tabfocus_elements: ":prev,:next",
	    dialog_type : "modal",
	    //theme_advanced_layout_manager : "SimpleLayout",
	    //theme_advanced_resizing : true,
	    //theme_advanced_resize_horizontal : false,
	    //theme_advanced_resizing_min_height : 30,
	    //theme_advanced_path: false,
	    theme_advanced_toolbar_align : "left",
	    theme_advanced_toolbar_location: "bottom",
	    theme_advanced_statusbar_location : "none",
	    theme_advanced_buttons1 : "charmap, italic, underline, bold, sub, sup, blockquote, link, " +
	                              "unlink, image, cleanup, code",
	    theme_advanced_buttons2 : "",
	    theme_advanced_buttons3 : ""
	    
	 });
*/	

		
	jQuery("body").bind(TapestryExt.EVT_BEFORE_ZONE_UPDATE, function() {
		//Remove ckeditor before ajax update
		Annotate.freeResources();
		//console.log("before zone update");
	});
	
	var disableLink = false;
	jQuery("#normalNotes").removeAttr("href").addClass("disable_link");
	
	jQuery("#normalNotes").click(function() {
		disableLink = false;
		jQuery(this).removeAttr("href").addClass("disable_link");
		jQuery("#innerNotes").attr("href", "#").removeClass("disable_link");
		
	});
	
	jQuery("#innerNotes").click(function() {
		disableLink = true;
		jQuery(this).removeAttr("href").addClass("disable_link");
		jQuery("#normalNotes").attr("href", "#").removeClass("disable_link");
	});
	
    jQuery('.notecontent').live('click',
        function(event) {
            if (!disableLink) {
            	var classes = jQuery(this).attr('class').replace(/notecontent\ /g,'').replace(/\ /g, '/');
            	jQuery(":input[name='selectedNoteId']").val(classes);
    			TapestryExt.submitZoneForm(jQuery("#selectNoteForm").get(0));
            }
        }    
    );
    
    //jQuery(".selectable-note").live('click',
    //	function(event) {
    //		alert("selected");
    //	}
    //);
    
    // live updated
    //jQuery('.notelink').live('click',
    //	function(event) {
    //		var localId= jQuery(this).attr('href').replace('#start','');
	//		var noteId = jQuery(this).attr('id').replace('noteid', '');		
    //		//Editor.updateEditZone("e" + localId + "/" + noteId);
    //	}
    //);
  
    jQuery('#createTermLink').bind('click', function() { Annotate.createNote() });
    jQuery("#connectTermLink").bind("click", function() { Annotate.connectNote() });
    jQuery('body').bind('mouseup', function() { Annotate.updateSelectionLinks() });
    jQuery('body').bind('mousemove', function() { Annotate.clearSelectionLinks() });
    
    
    /* TODO disable for note editing!
    jQuery(document).keydown(function(event) {
    	if (event.which == 65) {
    		createNote();
    	}
    });
    */
   
    jQuery(".jqmOpen").click(function() {
    	jQuery("#dialogZone").text("Hetki...");
    	jQuery("#dialog").jqm().jqmShow();
    });

    jQuery(".jqmClose").click(function() {
    	jQuery("#dialog").jqmHide();
    });
    
    
});
