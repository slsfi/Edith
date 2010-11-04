/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.util.Collection;
import java.util.HashSet;

import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;

/**
 * DocumentsPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class DocumentsPage {

    @Inject
    private DocumentRepository documentRepository;

    @Property
    private Collection<Document> documents;

    @Property
    private Document document;

    @SessionState(create=false)
    private Collection<Document> selectedDocuments;

    void onActivate(){
        if (selectedDocuments == null){
            selectedDocuments = new HashSet<Document>();
        }
        documents = documentRepository.getAll();
    }

    public boolean isDocumentSelected() {
        return selectedDocuments.contains(document);
    }

    public void setDocumentSelected(boolean selected) {
        if (selected) {
            selectedDocuments.add(document);
        } else {
            selectedDocuments.remove(document);
        }
    }

}
