/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNoteRevision.noteRevision;

import java.util.Arrays;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.util.Assert;

import com.mysema.query.BooleanBuilder;
import com.mysema.query.types.path.PString;
import com.mysema.rdfbean.dao.AbstractRepository;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionRepositoryImpl extends AbstractRepository<NoteRevision> implements NoteRevisionRepository {
    
    public NoteRevisionRepositoryImpl() {
        super(noteRevision);
    }
        
    @Override
    public GridDataSource queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);        
        BooleanBuilder builder = new BooleanBuilder();        
        if (!searchTerm.equals("*")){
            for (PString path : Arrays.asList(
                    noteRevision.lemma, 
                    noteRevision.longText,
                    noteRevision.basicForm,
                    noteRevision.revisionOf.term.meaning,
                    noteRevision.description
                    )){
                builder.or(path.contains(searchTerm, false));
            }    
        }        
        builder.and(noteRevision.eq(noteRevision.revisionOf.latestRevision));
        return createGridDataSource(noteRevision, builder.getValue());
    }

    @Override
    public NoteRevision getByLocalId(Document document, long revision, String localId) {
        Assert.notNull(document);
        Assert.notNull(localId);
        return getSession().from(noteRevision)
            .where(noteRevision.revisionOf.document.eq(document),
                   noteRevision.revisionOf.localId.eq(localId),
                   noteRevision.svnRevision.eq(revision))
            .uniqueResult(noteRevision);
    }
    
}
