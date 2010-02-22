/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNoteRevision.noteRevision;

import java.util.Arrays;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.expr.EBoolean;
import com.mysema.query.types.path.PEntity;
import com.mysema.query.types.path.PString;
import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.BeanSubQuery;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.QNoteRevision;
import fi.finlit.edith.domain.UserInfo;
import fi.finlit.edith.domain.UserRepository;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionRepositoryImpl extends AbstractRepository<NoteRevision> implements NoteRevisionRepository {

    private static final QNoteRevision otherNote = new QNoteRevision("other");

    private final TimeService timeService;

    private final UserRepository userRepository;

    public NoteRevisionRepositoryImpl(
            @Inject SessionFactory sessionFactory,
            @Inject UserRepository userRepository,
            @Inject TimeService timeService) {
        super(sessionFactory, noteRevision);
        this.userRepository = userRepository;
        this.timeService = timeService;
    }

    @Override
    public NoteRevision getByLocalId(DocumentRevision docRevision, String localId) {
        Assert.notNull(docRevision);
        Assert.notNull(localId);
        return getSession().from(noteRevision)
            .where(noteRevision.revisionOf.document.eq(docRevision.getDocument()),
                   noteRevision.revisionOf.localId.eq(localId),
                   noteRevision.svnRevision.loe(docRevision.getRevision()),
                   noteRevision.deleted.eq(false),
                   latestFor(docRevision.getRevision()))
            .uniqueResult(noteRevision);
    }

    @Override
    public List<NoteRevision> getOfDocument(DocumentRevision docRevision) {
        Assert.notNull(docRevision);
        return getSession().from(noteRevision)
            .where(noteRevision.revisionOf.document.eq(docRevision.getDocument()),
                   noteRevision.svnRevision.loe(docRevision.getRevision()),
                   noteRevision.deleted.eq(false),
                   latestFor(docRevision.getRevision()))
            .orderBy(noteRevision.longText.asc())
            .list(noteRevision);
    }

    private EBoolean latestFor(long svnRevision){
        return sub(otherNote).where(
            otherNote.ne(noteRevision),
            otherNote.revisionOf.eq(noteRevision.revisionOf),
            otherNote.svnRevision.loe(svnRevision),
            otherNote.createdOn.gt(noteRevision.createdOn)).notExists();
    }

    @Override
    public GridDataSource queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);
        BooleanBuilder builder = new BooleanBuilder();
        if (!searchTerm.equals("*")){
            for (PString path : Arrays.asList(
                    noteRevision.lemma,
                    noteRevision.longText,
                    noteRevision.revisionOf.term.basicForm,
                    noteRevision.revisionOf.term.meaning,
                    noteRevision.description,
                    noteRevision.subtextSources
                    )){
                builder.or(path.contains(searchTerm, false));
            }
        }
        builder.and(noteRevision.eq(noteRevision.revisionOf.latestRevision));
        builder.and(noteRevision.deleted.eq(false));
        return createGridDataSource(noteRevision,
                noteRevision.revisionOf.term.basicForm.asc(),
                builder.getValue());
    }

    @Override
    public NoteRevision save(NoteRevision note) {
        UserInfo createdBy = userRepository.getCurrentUser();
        note.setCreatedOn(timeService.currentTimeMillis());
        note.setCreatedBy(createdBy);
        note.getRevisionOf().setLatestRevision(note);
        getSession().save(note);
        getSession().save(note.getRevisionOf());
        return note;
    }

    private BeanSubQuery sub(PEntity<?> entity){
        return new BeanSubQuery().from(entity);
    }

}
