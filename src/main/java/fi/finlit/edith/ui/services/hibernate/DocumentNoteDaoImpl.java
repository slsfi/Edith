/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;


import static fi.finlit.edith.sql.domain.QDocumentNote.documentNote;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.User;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.ServiceException;
import fi.finlit.edith.ui.services.UserDao;

public class DocumentNoteDaoImpl extends AbstractDao<DocumentNote> implements
        DocumentNoteDao {

    private final UserDao userDao;

    public DocumentNoteDaoImpl(@Inject UserDao userDao) {
        this.userDao = userDao;
    }

    @Override
    public List<DocumentNote> getOfDocument(Document document) {
        Assert.notNull(document);
        return query()
                .from(documentNote)
                .where(documentNote.document.eq(document),
                        // FIXME: Commented out, is good?
//                       documentNote.revision.loe(),
                       documentNote.deleted.eq(false))
                .orderBy(documentNote.position.asc()).list(documentNote);
    }

    @Override
    public List<DocumentNote> getPublishableNotesOfDocument(Document document) {
        return query()
            .from(documentNote)
            .where(documentNote.document.eq(document),
                    // FIXME: Commented out, is good?
//                   documentNote.reevision.loe(docRevision.getRevision()),
                   documentNote.deleted.isFalse(),
                   documentNote.publishable.isTrue())
            .orderBy(documentNote.position.asc()).list(documentNote);
    }

    @Override
    //XXX This is not really used anywhere?
    public void remove(DocumentNote docNote) {
        Assert.notNull(docNote, "note was null");
        // XXX What was the point in having .createCopy?
        docNote.setDeleted(true);
        docNote.getNote().decDocumentNoteCount();
        //getSession().save(docNote);
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
        //getSession().save(docNote.getNote());
        //getSession().save(docNote);
        // FIXME: Hibernatify!
        //if (docNote.getNote().getComments() != null) {
        //    for (NoteComment comment : docNote.getNote().getComments()) {
        //        getSession().save(comment);
        //    }
        //}
        return docNote;
    }

    @Override
    public List<DocumentNote> getOfNote(Long noteId) {
        Assert.notNull(noteId);
        return query()
            .from(documentNote)
            .where(documentNote.note.id.eq(noteId),
                   documentNote.deleted.isFalse()).list(documentNote);
    }


    @Override
    public int getDocumentNoteCount(Note note) {
        return note.getDocumentNoteCount();
    }

    @Override
    public long getNoteCountForDocument(Long id) {
        return query()
            .from(documentNote)
            .where(documentNote.document.id.eq(id),
                   documentNote.deleted.isFalse())
            .count();
    }

    @Override
    public List<DocumentNote> getOfTerm(Long termId) {
        Assert.notNull(termId);
        return query()
            .from(documentNote)
            .where(documentNote.note.term.id.eq(termId),
                   documentNote.deleted.isFalse()).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPerson(Long personId) {
        Assert.notNull(personId);
        return query()
            .from(documentNote)
            .where(documentNote.note.person.id.eq(personId),
                   documentNote.deleted.isFalse()).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPlace(Long placeId) {
        Assert.notNull(placeId);
        return query()
            .from(documentNote)
            .where(documentNote.note.place.id.eq(placeId),
                   documentNote.deleted.isFalse()).list(documentNote);
    }

    @Override
    public DocumentNote getById(Long id) {
        return (DocumentNote) getSession().get(DocumentNote.class, id);
    }

}
