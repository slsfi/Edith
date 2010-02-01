/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;


import org.apache.tapestry5.Asset;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.ui.services.PrimaryKeyEncoder;

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
    private String editMode;

    private Context context;

    @Property
    private GridDataSource notes;

    @Property
    private NoteRevision note;

    @Inject 
    private NoteRevisionRepository noteRevisionRepo;

    @Property
    private PrimaryKeyEncoder encoder;

    @Inject
    @Path("NoteSearchPage.css")
    private Asset stylesheet;

    @Environmental
    private RenderSupport support;

    void onPrepare() {
        encoder = new PrimaryKeyEncoder(noteRevisionRepo);
    }

    void onActionFromToggleEdit() {
        context = new Context(searchTerm, "edit");
    }

    void onActionFromCancel() {
        context = new Context(searchTerm);
    }

    void onActionFromDelete(String noteRevisionId) {
        noteRevisionRepo.remove(noteRevisionId);
    }

    void onActivate(EventContext context) {
        if (context.getCount() >= 1) {
            this.searchTerm = context.get(String.class, 0);
        }
        if (context.getCount() >= 2) {
            this.editMode = context.get(String.class, 1);
        }
        this.context = new Context(context);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    void onSuccessFromEdit() {
        // TODO Validations
        noteRevisionRepo.saveAll(encoder.getAllValues());
        context = new Context(searchTerm);
    }

    void setupRender() {
        notes = noteRevisionRepo.queryNotes(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    @AfterRender
    void addStylesheet() {
        // This is needed to have the page specific style sheet after
        // other css includes
        support.addStylesheetLink(stylesheet, null);
    }
    
}
