/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * PrintPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@IncludeStylesheet({
    "context:styles/base.css",
    "context:styles/edith.css",       
    "context:styles/tei.css",

    "context:styles/skins/printer-friendly.css"    
})
@SuppressWarnings("unused")    
public class PrintPage {

    @Inject
    private DocumentRepository documentRepo;
    
    @Property
    private Document document;
    
    @Property
    private DocumentRevision documentRevision;
    
    void onActivate(String id){
        document = documentRepo.getById(id);
        documentRevision = new DocumentRevision(document, -1);
    }
}
