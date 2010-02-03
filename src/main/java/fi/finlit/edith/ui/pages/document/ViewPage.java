/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Log;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * ViewPage provides
 *
 * @author tiwe
 * @version $Id$
 */
//@IncludeJavaScriptLibrary( { "classpath:jquery-1.3.2.js", "classpath:TapestryExt.js", "ViewPage.js" })
@IncludeStylesheet("context:styles/tei.css")
@SuppressWarnings("unused")
public class ViewPage extends AbstractDocumentPage{
    
    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Property
    private NoteRevision note;

    @Property
    private List<NoteRevision> docNotes;
    
    void setupRender() {
        docNotes = noteRevisionRepo.getOfDocument(getDocumentRevision());
    }
    
}
