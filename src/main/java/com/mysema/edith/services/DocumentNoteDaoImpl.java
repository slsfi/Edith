/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.util.List;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.commons.lang.Assert;
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
        return query().from(documentNote).where(documentNote.document.eq(document),
        // FIXME: Commented out, is good?
        // documentNote.revision.loe(),
                documentNote.deleted.eq(false)).orderBy(documentNote.position.asc())
                .list(documentNote);
    }

    @Override
    public List<DocumentNote> getPublishableNotesOfDocument(Document document) {
        return query().from(documentNote).where(documentNote.document.eq(document),
        // FIXME: Commented out, is good?
        // documentNote.reevision.loe(docRevision.getRevision()),
                documentNote.deleted.isFalse(), documentNote.publishable.isTrue())
                .orderBy(documentNote.position.asc()).list(documentNote);
    }
    
    @Override
    public void remove(Long id) {
        DocumentNote docNote = find(DocumentNote.class, id);
        if (docNote != null) {
            remove(docNote);
        }
    }

    @Override
    // XXX This is not really used anywhere?
    public void remove(DocumentNote docNote) {
        // XXX What was the point in having .createCopy?
        docNote.setDeleted(true);
        docNote.getNote().decDocumentNoteCount();
        // getSession().save(docNote);
    }

    @Override
    public DocumentNote save(DocumentNote docNote) {
        if (docNote.getNote() == null) {
            throw new ServiceException("Note was null for " + docNote);
        }
        User createdBy = userDao.getCurrentUser();
        long currentTime = System.currentTimeMillis();
        docNote.setCreatedOn(currentTime);
        docNote.getNote().setEditedOn(currentTime);
        docNote.getNote().setLastEditedBy(createdBy);
        docNote.getNote().getAllEditors().add(createdBy);
        // getSession().save(docNote.getNote());
        // getSession().save(docNote);
        // FIXME: Hibernatify!
        // if (docNote.getNote().getComments() != null) {
        // for (NoteComment comment : docNote.getNote().getComments()) {
        // getSession().save(comment);
        // }
        // }
        return docNote;
    }

    @Override
    public List<DocumentNote> getOfNote(Long noteId) {
        return query().from(documentNote)
                .where(documentNote.note.id.eq(noteId), documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public int getDocumentNoteCount(Note note) {
        return note.getDocumentNoteCount();
    }

    @Override
    public long getNoteCountForDocument(Long id) {
        return query().from(documentNote)
                .where(documentNote.document.id.eq(id), documentNote.deleted.isFalse()).count();
    }

    @Override
    public List<DocumentNote> getOfTerm(Long termId) {
        return query().from(documentNote)
                .where(documentNote.note.term.id.eq(termId), documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPerson(Long personId) {
        return query().from(documentNote)
                .where(documentNote.note.person.id.eq(personId), documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPlace(Long placeId) {
        return query().from(documentNote)
                .where(documentNote.note.place.id.eq(placeId), documentNote.deleted.isFalse())
                .list(documentNote);
    }

    @Override
    public DocumentNote getById(Long id) {
        return find(DocumentNote.class, id);
    }

}
