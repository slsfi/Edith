/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.QDocumentNote;
import com.mysema.edith.domain.User;

@Transactional
public class DocumentNoteDaoImpl extends AbstractDao<DocumentNote> implements DocumentNoteDao {

    private static final QDocumentNote documentNote = QDocumentNote.documentNote;
    
    private final UserDao userDao;

    @Inject
    public DocumentNoteDaoImpl(UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<DocumentNote> getOfDocument(Document document) {
        return from(documentNote)
            .where(
                documentNote.document.eq(document),
                documentNote.deleted.eq(false))
             .orderBy(documentNote.position.asc())
             .list(documentNote);
    }
    
    @Override
    public List<DocumentNote> getOfDocument(Long docId) {
        return from(documentNote)
            .where(
                documentNote.document.id.eq(docId),
                documentNote.deleted.eq(false))
            .orderBy(documentNote.position.asc())
            .list(documentNote);
    }

    @Override
    public List<DocumentNote> getPublishableNotesOfDocument(Document document) {
        return from(documentNote)
            .where(
                documentNote.document.eq(document),
                documentNote.deleted.isFalse(), 
                documentNote.publishable.isTrue())
             .orderBy(documentNote.position.asc())
             .list(documentNote);
    }
    
    @Override
    public void remove(Long id) {
        DocumentNote docNote = find(DocumentNote.class, id);
        if (docNote != null) {
            remove(docNote);
        }
    }

    @Override
    public void remove(DocumentNote docNote) {
        docNote.setDeleted(true);
        docNote.getNote().decDocumentNoteCount();
        persistOrMerge(docNote);
    }

    @Override
    public DocumentNote save(DocumentNote docNote) {
        if (docNote.getId() == null) {
            docNote.setCreatedOn(System.currentTimeMillis());    
        }                
        return persistOrMerge(docNote);
    }

    @Override
    public List<DocumentNote> getOfNote(Long noteId) {
        return from(documentNote)
                .where(
                    documentNote.note.id.eq(noteId), 
                    documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public int getDocumentNoteCount(Note note) {
        return note.getDocumentNoteCount();
    }

    @Override
    public long getNoteCountForDocument(Long id) {
        return from(documentNote)
                .where(
                    documentNote.document.id.eq(id), 
                    documentNote.deleted.isFalse())
                .count();
    }

    @Override
    public List<DocumentNote> getOfTerm(Long termId) {
        return from(documentNote)
                .where(
                    documentNote.note.term.id.eq(termId), 
                    documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPerson(Long personId) {
        return from(documentNote)
                .where(
                    documentNote.note.person.id.eq(personId), 
                    documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPlace(Long placeId) {
        return from(documentNote)
                .where(
                    documentNote.note.place.id.eq(placeId), 
                    documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public DocumentNote getById(Long id) {
        return find(DocumentNote.class, id);
    }

}
