/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QNote.note;
import static fi.finlit.edith.domain.QNoteRevision.noteRevision;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.util.Assert;

import com.mysema.query.paging.CallbackService;
import com.mysema.query.paging.ListSource;
import com.mysema.rdfbean.tapestry.PagedQuery;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;

/**
 * NoteRepositoryImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryImpl extends AbstractRepository<Note> implements NoteRepository{

    @Inject
    private CallbackService txCallback;
    
    public NoteRepositoryImpl() {
        super(note);
    }

    private PagedQuery getPagedQuery(){
        return new PagedQuery(txCallback, getSession());
    }
    
    //@Override
    public ListSource<NoteRevision> queryNotes(String searchTerm) {
        Assert.notNull(searchTerm);
        return getPagedQuery()
            .from(noteRevision)
            .where(noteRevision.lemma.contains(searchTerm, false))
            .orderBy(noteRevision.createdOn.desc())
            .list(noteRevision); 
    }
}
