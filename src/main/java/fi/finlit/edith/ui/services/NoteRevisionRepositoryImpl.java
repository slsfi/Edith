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
                    noteRevision.meaning,
                    noteRevision.explanation
                    )){
                builder.or(path.contains(searchTerm, false));
            }    
        }
        builder.and(noteRevision.latestRevisionOf.isNotNull());
        return createGridDataSource(noteRevision, builder.getValue());
    }
    
}
