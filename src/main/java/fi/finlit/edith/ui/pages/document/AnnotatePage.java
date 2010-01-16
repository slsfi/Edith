/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * AnnotatePage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.3.2.js", "classpath:TapestryExt.js", "AnnotatePage.js"})
@IncludeStylesheet("context:styles/tei.css")
public class AnnotatePage extends AbstractDocumentPage{
    
    @Inject
    private Block noteEditForm;
    
    @Property
    private String context;    
    
    @Inject
    private RenderSupport renderSupport;
    
    @Inject
    private ComponentResources resources;

    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }


    Object onEdit(EventContext context){
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < context.getCount(); i++){
            if (i > 0) builder.append(", ");
            builder.append(context.get(String.class, i));
        }
        this.context = builder.toString();
        return noteEditForm;
    }
    
}
