/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocumentNote.documentNote;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.path.StringPath;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.domain.QNote;
import fi.finlit.edith.domain.UserInfo;

public class DocumentNoteRepositoryImpl extends AbstractRepository<DocumentNote> implements
        DocumentNoteRepository {

    private final TimeService timeService;

    private final UserRepository userRepository;

    private final boolean extendedTerm;

    public DocumentNoteRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository,
            @Inject TimeService timeService,
            @Inject @Symbol(EDITH.EXTENDED_TERM) boolean extendedTerm) {
        super(sessionFactory, documentNote);
        this.userRepository = userRepository;
        this.timeService = timeService;
        this.extendedTerm = extendedTerm;
        System.err.println(extendedTerm);
    }

    @Override
    public List<DocumentNote> getOfDocument(DocumentRevision docRevision) {
        Assert.notNull(docRevision);
        return getSession()
                .from(documentNote)
                .where(documentNote.document().eq(docRevision.getDocument()),
                       documentNote.svnRevision.loe(docRevision.getRevision()),
                       documentNote.deleted.eq(false))
                .orderBy(documentNote.createdOn.asc()).list(documentNote);
    }

    @Override
    public List<DocumentNote> getPublishableNotesOfDocument(DocumentRevision docRevision) {
        return getSession()
            .from(documentNote)
            .where(documentNote.document().eq(docRevision.getDocument()),
                   documentNote.svnRevision.loe(docRevision.getRevision()),
                   documentNote.deleted.isFalse(),
                   documentNote.publishable.isTrue())
            .orderBy(documentNote.position.asc()).list(documentNote);
    }

    @Override
    public GridDataSource queryNotes(String searchTerm) {
        QDocumentNote documentNote = QDocumentNote.documentNote;
        QNote note = documentNote.note();
        Assert.notNull(searchTerm);
        BooleanBuilder builder = new BooleanBuilder();
        if (!searchTerm.equals("*")) {
            for (StringPath path : Arrays.asList(
                    note.lemma,
                    documentNote.longText,
                    note.term().basicForm,
                    note.term().meaning)) {
                // ,
                // documentNote.description, FIXME
                // note.subtextSources)
                builder.or(path.containsIgnoreCase(searchTerm));
            }
        }
        builder.and(documentNote.deleted.eq(false));

        return createGridDataSource(documentNote, note.term().basicForm.lower().asc(), false, builder.getValue());
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
    public void remove(String documentNoteId) {
        DocumentNote note = getById(documentNoteId);
        remove(note);
    }

    @Override
    public DocumentNote save(DocumentNote docNote) {
        if (docNote.getNote() == null) {
            throw new ServiceException("Note was null for " + docNote);
        }
        UserInfo createdBy = userRepository.getCurrentUser();
        long currentTime = timeService.currentTimeMillis();
        docNote.setCreatedOn(currentTime);
        docNote.getNote().setEditedOn(currentTime);
        if (docNote.getConcept(extendedTerm) != null) {
            docNote.getConcept(extendedTerm).setLastEditedBy(createdBy);
            docNote.getConcept(extendedTerm).getAllEditors().add(createdBy);
        }
        getSession().save(docNote.getNote());
        getSession().save(docNote);
        if (docNote.getConcept(extendedTerm) != null && docNote.getConcept(extendedTerm).getComments() != null) {
            for (NoteComment comment : docNote.getConcept(extendedTerm).getComments()) {
                getSession().save(comment);
            }
        }

        return docNote;
    }

    @Override
    //XXX Not really used
    public DocumentNote saveAsCopy(DocumentNote docNote) {
        if (docNote.getNote() == null) {
            throw new ServiceException("Note was null for " + docNote);
        }
        docNote.setNote(docNote.getNote().createCopy());
        docNote.getNote().incDocumentNoteCount();
        return save(docNote);
    }

    @Override
    public List<DocumentNote> getOfNotes(Collection<Note> notes){
        return getSession()
        .from(documentNote)
        .where(documentNote.note().in(notes),
               documentNote.deleted.eq(false)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfNote(String noteId) {
        Assert.notNull(noteId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().id.eq(noteId),
                       documentNote.deleted.eq(false)).list(documentNote);
    }


    @Override
    public int getDocumentNoteCount(Note note) {
        return (int)getSession()
            .from(documentNote)
            .where(documentNote.note().eq(note),
               documentNote.deleted.eq(false)).count();
    }

    @Override
    public long getNoteCountForDocument(String id) {
        return getSession()
            .from(documentNote)
            .where(documentNote.document().id.eq(id),
                   documentNote.deleted.isFalse())
            .count();
    }


    @Override
    public List<DocumentNote> getOfNoteInDocument(String noteId, String documentId) {
        Assert.notNull(noteId);
        Assert.notNull(documentId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().id.eq(noteId),
                       documentNote.document().id.eq(documentId),
                       documentNote.deleted.eq(false)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfTerm(String termId) {
        Assert.notNull(termId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().term().id.eq(termId),
                       documentNote.deleted.eq(false)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPerson(String personId) {
        Assert.notNull(personId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().person().id.eq(personId),
                       documentNote.deleted.eq(false)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPlace(String placeId) {
        Assert.notNull(placeId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().place().id.eq(placeId),
                       documentNote.deleted.eq(false)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getNotesLessDocumentNotes() {
        return getSession().from(documentNote).where(documentNote.note().isNull()).list(documentNote);
    }

}
