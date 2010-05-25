/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.TermWithNotes;

/**
 * DictionarySearchPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class DictionarySearchPage {

    @Property
    private String searchTerm;

    private Context context;

    @Property
    private GridDataSource terms;

    @Property
    private TermWithNotes term;
    
    @Property
    private Note note;

    @Inject 
    private NoteRepository noteRepo;

    @Environmental
    private RenderSupport support;

    void onActivate(EventContext ctx) {
        if (ctx.getCount() >= 1) {
            this.searchTerm = ctx.get(String.class, 0);
        }
        this.context = new Context(ctx);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    void setupRender() {
        terms = noteRepo.queryDictionary(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }


}
