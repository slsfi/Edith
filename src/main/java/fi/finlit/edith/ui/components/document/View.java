/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components.document;

import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.DocumentRenderer;

/**
 * ViewPanel provides
 *
 * @author tiwe
 * @version $Id$
 */
@IncludeJavaScriptLibrary( { "classpath:jquery-1.3.2.js"})
public class View {

    @Environmental
    private RenderSupport renderSupport;
    
    @Inject
    private DocumentRenderer renderer;
    
    @Parameter
    private DocumentRevision document;
    
    @Inject
    private ComponentResources resources;

    @BeginRender
    void beginRender(MarkupWriter writer) throws Exception {
        renderer.renderDocument(document, writer);
    }
    
    @AfterRender
    void addScript() {
        // TODO : create an onClick listener for .notecontent elements that onEdit events
//        resources.createEventLink(eventType, context)
//        renderSupport.addScript(script);
    }
    
    void onEdit(){
        // TODO : return MultiZoneUpdate for form edits
    }
}
