/*
 * Copyright (c) 2018 Mysema
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
        long currentTime = System.currentTimeMillis();
        if (docNote.getId() == null) {
            docNote.setCreatedOn(currentTime);    
        }                
        if (docNote.getNote() != null) {
            User createdBy = userDao.getCurrentUser(); 
            if (createdBy != null) {
                docNote.getNote().setEditedOn(currentTime);
                docNote.getNote().setLastEditedBy(createdBy);
                docNote.getNote().getAllEditors().add(createdBy);    
            } else {
                throw new IllegalStateException("No current user");
            }                      
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
    public Long getLastNoteTimestampForDocument(Long id) {
        List<Long> editedOn = from(documentNote)
                                .where(
                                    documentNote.document.id.eq(id), 
                                    documentNote.deleted.isFalse())
                                .orderBy(documentNote.note.editedOn.desc())
                                .limit(1)
                                .list(documentNote.note.editedOn);

        return (editedOn.isEmpty() ? 0 : editedOn.get(0));
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
