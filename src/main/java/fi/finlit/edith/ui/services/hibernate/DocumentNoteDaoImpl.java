/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.hibernate;


import static fi.finlit.edith.sql.domain.QDocumentNote.documentNote;
import static fi.finlit.edith.sql.domain.QTerm.term;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.jpa.JPQLQuery;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteComment;
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
                .orderBy(documentNote.createdOn.asc()).list(documentNote);
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
    public GridDataSource queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);
        JPQLQuery q = query();
        BooleanBuilder builder = new BooleanBuilder();
        if (!searchTerm.equals("*")) {
            
            builder.or(documentNote.note.lemma.containsIgnoreCase(searchTerm));
            builder.or(documentNote.fullSelection.containsIgnoreCase(searchTerm));
            
            
            
            //builder.or(documentNote.note.term.basicForm.containsIgnoreCase(searchTerm));
            //builder.or(documentNote.note.term.meaning.containsIgnoreCase(searchTerm));
//            for (StringPath path : Arrays.asList(
//                    documentNote.note.lemma,
//                    documentNote.fullSelection,
//                    documentNote.note.term.basicForm,
//                    documentNote.note.term.meaning)) {
//                // ,
//                // documentNote.description, FIXME
//                // note.subtextSources)
//                builder.or(path.containsIgnoreCase(searchTerm));
//            }
            builder.or(
                sub().from(term)
                .where(
                  term.id.eq(documentNote.note.id),
                  term.basicForm.containsIgnoreCase(searchTerm).or(
                  term.meaning.containsIgnoreCase(searchTerm)))
                .exists()
            );
            
        }
        builder.and(documentNote.deleted.eq(false));

        return createGridDataSource(documentNote, term.basicForm.lower().asc(), false, builder.getValue());
    }

    @Override
    //XXX This is not really used anywhere?
    public void remove(DocumentNote docNote) {
        Assert.notNull(docNote, "note was null");
        // XXX What was the point in having .createCopy?
        docNote.setDeleted(true);
        docNote.getNote().decDocumentNoteCount();
        getSession().save(docNote);
    }

    @Override
    public void remove(Long documentNoteId) {
        // FIXME: Hibernatify!
        DocumentNote note = getById(documentNoteId);
        remove(note);
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
        getSession().save(docNote.getNote());
        getSession().save(docNote);
        // FIXME: Hibernatify!
        if (docNote.getNote().getComments() != null) {
            for (NoteComment comment : docNote.getNote().getComments()) {
                getSession().save(comment);
            }
        }
        return docNote;
    }

//    @Override
//    //XXX Not really used
//    public DocumentNote saveAsCopy(DocumentNote docNote) {
//        if (docNote.getNote() == null) {
//            throw new ServiceException("Note was null for " + docNote);
//        }
//        docNote.setNote(docNote.getNote().createCopy());
//        docNote.getNote().incDocumentNoteCount();
//        return save(docNote);
//    }

    @Override
    public List<DocumentNote> getOfNotes(Collection<Note> notes){
        return query()
            .from(documentNote)
            .where(documentNote.note.in(notes),
                   documentNote.deleted.isFalse()).list(documentNote);
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
//        return (int)query()
//            .from(documentNote)
//            .where(documentNote.note.eq(note),
//               documentNote.deleted.isFalse()).count();
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
    public List<DocumentNote> getOfNoteInDocument(Long noteId, Long documentId) {
        Assert.notNull(noteId);
        Assert.notNull(documentId);
        return query()
            .from(documentNote)
            .where(documentNote.note.id.eq(noteId),
                   documentNote.document.id.eq(documentId),
                   documentNote.deleted.isFalse()).list(documentNote);
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
    public List<DocumentNote> getNotesLessDocumentNotes() {
        return query()
            .from(documentNote)
            .where(documentNote.note.isNull())
            .list(documentNote);
    }

    @Override
    public Collection<DocumentNote> getAll() {
        return query()
            .from(documentNote)
            .list(documentNote);
    }

    @Override
    public DocumentNote getById(Long id) {
        return (DocumentNote) getSession().get(DocumentNote.class, id);
    }

}
