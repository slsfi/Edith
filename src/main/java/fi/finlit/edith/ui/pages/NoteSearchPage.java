/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.query.paging.ListSource;
import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;

/**
 * NoteSearch provides
 * 
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class NoteSearchPage {

    @Property
    private String searchTerm;

    @Property
    private ListSource<NoteRevision> notes;

    @Property
    private NoteRevision note;

    @Inject
    private NoteRepository noteRepository;
    
    void onActivate(EventContext context){
        if (context.getCount() > 0){
            searchTerm = context.get(String.class, 0);
            notes = noteRepository.queryNotes(searchTerm);
        }  
    }
    
    Object onPassivate(){
        return searchTerm != null ? searchTerm : "*";
    }

}
