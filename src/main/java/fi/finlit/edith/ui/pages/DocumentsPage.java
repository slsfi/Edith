/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.util.Collection;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

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
    
    void onActivate(){
        documents = documentRepo.getAll();
    }
    
    public void onRemoveNotes(String docId){
        Document document = documentRepo.getById(docId);
        if (document != null){
            documentRepo.removeAllNotes(document);    
        }else{
            throw new IllegalArgumentException("Got no document for id " + docId);
        }
        
    }

}
