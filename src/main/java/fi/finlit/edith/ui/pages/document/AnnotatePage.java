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
 * AnnotatePage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
@IncludeStylesheet("context:styles/tei.css")
public class AnnotatePage {
    
    @Inject
    private DocumentRepository documentRepo;
       
    @Property
    private Document document;
    
    @Property
    private DocumentRevision documentRevison;
    
    void onActivate(String id){
        document = documentRepo.getById(id);
        documentRevison = new DocumentRevision(document, -1);
    }
    

}
