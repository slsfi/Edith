/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import java.util.Collection;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;

/**
 * DocumentsPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class DocumentsPage {
    
    @Inject
    private DocumentRepository documentRepo;
    
    @Property
    private Collection<Document> documents;
    
    @Property
    private Document document;
    

    @Secured("ROLE_USER")
    void onActivate(){
        documents = documentRepo.getAll();
    }
    
}
