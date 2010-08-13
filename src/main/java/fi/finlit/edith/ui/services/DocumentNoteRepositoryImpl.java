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
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.OrderSpecifier;
import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.expr.EComparableBase;
import com.mysema.query.types.path.PEntity;
import com.mysema.query.types.path.PString;
import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.BeanSubQuery;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
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

    private final NoteRepository noteRepository;

    public DocumentNoteRepositoryImpl(@Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository, @Inject TimeService timeService,
            @Inject NoteRepository noteRepository) {
        super(sessionFactory, documentNote);
        this.userRepository = userRepository;
        this.timeService = timeService;
        this.noteRepository = noteRepository;
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
        return sub(otherNote).where(otherNote.ne(docNote), otherNote.note().eq(docNote.note()),
                otherNote.createdOn.gt(docNote.createdOn)).notExists();
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
    public void remove(DocumentNote note) {
        Assert.notNull(note, "note was null");
        DocumentNote deleted = note.createCopy();
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
        Note note = noteRepository.find(docNote.getNote().getLemma());
        if (note != null && !docNote.getNote().equals(note)) {
            docNote.setNote(note);
        }
        getSession().save(docNote);
        return docNote;
    }

    private BeanSubQuery sub(PEntity<?> entity) {
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
//        if (!searchInfo.getDocuments().isEmpty()) {
//            filters.and(documentNote.document().in(searchInfo.getDocuments()));
//        }
        // creators
        if (!searchInfo.getCreators().isEmpty()) {
            Collection<String> usernames = new ArrayList<String>(searchInfo.getCreators().size());
            for (UserInfo userInfo : searchInfo.getCreators()) {
                usernames.add(userInfo.getUsername());
            }
            filters.and(documentNote.createdBy().username.in(usernames));
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
        filters.and(latest(documentNote));

        BeanQuery query = getSession().from(documentNote)
                .where(documentNote.note().isNotNull(), filters).orderBy(getOrderBy(searchInfo));
        // TODO Status
        return query.list(documentNote);
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
        BeanQuery query = getSession().from(documentNote).where(documentNote.note().id.eq(noteId),
                documentNote.deleted.eq(false), latest(documentNote));
        return query.list(documentNote);
    }
}
