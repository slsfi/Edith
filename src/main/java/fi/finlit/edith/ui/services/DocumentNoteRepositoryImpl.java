/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QDocumentNote.documentNote;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.EntityPath;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.expr.EComparableBase;
import com.mysema.query.types.path.PString;
import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.BeanSubQuery;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.domain.QNote;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.domain.UserRepository;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentNoteRepositoryImpl extends AbstractRepository<DocumentNote> implements
        DocumentNoteRepository {

    private static final QDocumentNote otherNote = new QDocumentNote("other");

    private final TimeService timeService;

    private final UserRepository userRepository;

    public DocumentNoteRepositoryImpl(@Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository, @Inject TimeService timeService) {
        super(sessionFactory, documentNote);
        this.userRepository = userRepository;
        this.timeService = timeService;
    }

    @Override
    public DocumentNote getByLocalId(DocumentRevision docRevision, String localId) {
        Assert.notNull(docRevision);
        Assert.notNull(localId);
        return getSession()
                .from(documentNote)
                .where(documentNote.document().eq(docRevision.getDocument()),
                        documentNote.localId.eq(localId),
                        documentNote.svnRevision.loe(docRevision.getRevision()),
                        documentNote.deleted.eq(false),
                        latestFor(documentNote, docRevision.getRevision()))
                .uniqueResult(documentNote);
    }

    @Override
    public List<DocumentNote> getOfDocument(DocumentRevision docRevision) {
        Assert.notNull(docRevision);
        return getSession()
                .from(documentNote)
                .where(documentNote.document().eq(docRevision.getDocument()),
                        documentNote.svnRevision.loe(docRevision.getRevision()),
                        documentNote.deleted.eq(false),
                        latestFor(documentNote, docRevision.getRevision()))
                .orderBy(documentNote.createdOn.asc()).list(documentNote);
    }

    private EBoolean latestFor(QDocumentNote docNote, long svnRevision) {
        return sub(otherNote).where(otherNote.ne(docNote),
                // otherNote.note().eq(documentNote.note()),
                otherNote.localId.eq(docNote.localId), otherNote.svnRevision.loe(svnRevision),
                otherNote.createdOn.gt(docNote.createdOn)).notExists();
    }

    private EBoolean latest(QDocumentNote docNote) {
        return sub(otherNote).where(otherNote.ne(docNote), otherNote.localId.eq(docNote.localId),
                otherNote.note().eq(docNote.note()), otherNote.createdOn.gt(docNote.createdOn))
                .notExists();
    }

    @Override
    public GridDataSource queryNotes(String searchTerm) {
        QNote note = documentNote.note();
        Assert.notNull(searchTerm);
        BooleanBuilder builder = new BooleanBuilder();
        if (!searchTerm.equals("*")) {
            for (PString path : Arrays.asList(note.lemma, documentNote.longText,
                    note.term().basicForm, note.term().meaning,
                    // documentNote.description, FIXME
                    note.subtextSources)) {
                builder.or(path.contains(searchTerm, false));
            }
        }
        builder.and(documentNote.deleted.eq(false));
        builder.and(latest(documentNote));

        return createGridDataSource(documentNote, note.term().basicForm.lower().asc(), false,
                builder.getValue());
    }

    @Override
    public void remove(DocumentNote docNote) {
        Assert.notNull(docNote, "note was null");
        // XXX What was the point in having .createCopy?
        DocumentNote deleted = docNote;
        deleted.setDeleted(true);
        deleted.setCreatedBy(userRepository.getCurrentUser());
        getSession().save(deleted);
    }

    @Override
    public void remove(String noteRevisionId) {
        DocumentNote note = super.getById(noteRevisionId);
        remove(note);
    }

    @Override
    public DocumentNote save(DocumentNote docNote) {
        UserInfo createdBy = userRepository.getCurrentUser();
        docNote.setCreatedOn(timeService.currentTimeMillis());
        docNote.setCreatedBy(createdBy);
        if (docNote.getEditors() == null) {
            docNote.setEditors(new HashSet<UserInfo>());
        }
        docNote.getEditors().add(createdBy);
        getSession().save(docNote);
        if (docNote.getDocument() != null) {
            getSession().flush();
            removeOrphans(docNote.getNote().getId());
        }

        return docNote;
    }

    @Override
    public DocumentNote saveAsCopy(DocumentNote docNote) {
        docNote.setNote(copy(docNote.getNote()));
        return save(docNote);
    }

    // TODO This doesn't belong here. Though getSession() does :/
    private Note copy(Note note) {
        Note copy = new Note();
        Set<NoteComment> comments = new HashSet<NoteComment>();
        for (NoteComment comment : note.getComments()) {
            NoteComment copyOfComment = comment.copy();
            copyOfComment.setNote(copy);
            comments.add(copyOfComment);
            getSession().save(copyOfComment);
        }
        copy.setComments(comments);
        if (note.getDescription() != null) {
            copy.setDescription(note.getDescription().copy());
        }
        copy.setFormat(note.getFormat());
        copy.setLemma(note.getLemma());
        copy.setLemmaMeaning(note.getLemmaMeaning());
        copy.setPerson(note.getPerson());
        copy.setPlace(note.getPlace());
        if (note.getSources() != null) {
            copy.setSources(note.getSources().copy());
        }
        copy.setSubtextSources(note.getSubtextSources());
        copy.setTerm(note.getTerm());
        copy.setTypes(note.getTypes());
        return copy;
    }

    private BeanSubQuery sub(EntityPath<?> entity) {
        return new BeanSubQuery().from(entity);
    }

    @Override
    public List<DocumentNote> query(DocumentNoteSearchInfo searchInfo) {
        Assert.notNull(searchInfo);
        EBoolean filters = new BooleanBuilder();
        filters.and(documentNote.deleted.eq(false));
        // document & orphans
        EBoolean documentAndOrphanFilter = null;
        if (!searchInfo.getDocuments().isEmpty() || searchInfo.isOrphans()) {
            documentAndOrphanFilter = new BooleanBuilder();
            if (!searchInfo.getDocuments().isEmpty()) {
                documentAndOrphanFilter.or(documentNote.document().in(searchInfo.getDocuments()));
            }
            if (searchInfo.isOrphans()) {
                documentAndOrphanFilter.or(documentNote.document().isNull());
            }
            filters.and(documentAndOrphanFilter);
        }
        // creators
        if (!searchInfo.getCreators().isEmpty()) {
            EBoolean filter = new BooleanBuilder();
            Collection<String> usernames = new ArrayList<String>(searchInfo.getCreators().size());
            for (UserInfo userInfo : searchInfo.getCreators()) {
                filter.or(documentNote.editors.contains(userInfo));
                usernames.add(userInfo.getUsername());
            }
            // FIXME This is kind of useless except that we have broken data in production.
            filter.or(documentNote.createdBy().username.in(usernames));
            filters.and(filter);
        }
        // formats
        if (!searchInfo.getNoteFormats().isEmpty()) {
            filters.and(documentNote.note().format().in(searchInfo.getNoteFormats()));
        }
        // types
        if (!searchInfo.getNoteTypes().isEmpty()) {
            EBoolean filter = new BooleanBuilder();
            for (NoteType type : searchInfo.getNoteTypes()) {
                filter.or(documentNote.note().types.contains(type));
            }
            filters.and(filter);
        }
        filters.and(sub(otherNote).where(otherNote.ne(documentNote),
                otherNote.note().eq(documentNote.note()),
                otherNote.createdOn.gt(documentNote.createdOn)).notExists());

        return getSession().from(documentNote).where(documentNote.note().isNotNull(), filters)
                .orderBy(getOrderBy(searchInfo)).list(documentNote);
        // TODO Status
    }

    private OrderSpecifier<?> getOrderBy(DocumentNoteSearchInfo searchInfo) {
        EComparableBase<?> comparable = null;
        switch (searchInfo.getOrderBy()) {
        case DATE:
            comparable = documentNote.createdOn;
            break;
        case USER:
            comparable = documentNote.createdBy().username.toLowerCase();
            break;
        default:
            comparable = documentNote.note().lemma.toLowerCase();
            break;
        }
        return searchInfo.isAscending() ? comparable.asc() : comparable.desc();
    }

    @Override
    public List<DocumentNote> getOfNote(String noteId) {
        Assert.notNull(noteId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().id.eq(noteId), documentNote.deleted.eq(false),
                        latest(documentNote)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfNoteInDocument(String noteId, String documentId) {
        Assert.notNull(noteId);
        Assert.notNull(documentId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().id.eq(noteId),
                        documentNote.document().id.eq(documentId), documentNote.deleted.eq(false),
                        latest(documentNote)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfTerm(String termId) {
        Assert.notNull(termId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().term().id.eq(termId), documentNote.deleted.eq(false),
                        latest(documentNote)).list(documentNote);
    }

    @Override
    public List<DocumentNote> getOfPerson(String personId) {
        Assert.notNull(personId);
        return getSession()
                .from(documentNote)
                .where(documentNote.note().person().id.eq(personId),
                        documentNote.deleted.eq(false), latest(documentNote)).list(documentNote);

    }

    @Override
    public void removeOrphans(String noteId) {
        for (DocumentNote current : getOfNote(noteId)) {
            if (current.getDocument() == null) {
                remove(current);
            }
        }
    }
}
