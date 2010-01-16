/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

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
    private List<NoteRevision> notes;
    
    @Property
    private NoteRevision note;
    
    @Inject
    private RenderSupport renderSupport;
    
    @Inject
    private ComponentResources resources;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Property
    private List<NoteRevision> docNotes;
    
    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

    void setupRender() {
        Document document = getDocument();
        DocumentRevision documentRevision = getDocumentRevision();
        docNotes = noteRevisionRepo.getOfDocument(document, documentRevision.getRevision());
    }

    Object onEdit(EventContext context){
        Document document = getDocument();
        DocumentRevision documentRevision = getDocumentRevision();
        notes = new ArrayList<NoteRevision>(context.getCount());        
        for (int i = 0; i < context.getCount(); i++){
            String localId = context.get(String.class, i).substring(1);
            NoteRevision rev = noteRevisionRepo.getByLocalId(document, documentRevision.getRevision(), localId);
            if (rev != null){
                notes.add(rev);    
            }            
        }
        return noteEditForm;
    }
    
}
