/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import java.util.Collection;
import java.util.HashSet;

import javax.naming.event.EventContext;
import javax.ws.rs.Path;

import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Note;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.NoteDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
@Import(library = { "classpath:js/jquery-1.4.1.js", "deleteDialog.js" })
public class NoteSearch {

    @Property
    private String searchTerm;

    @Property
    private String editMode;

    private Context context;

    @Property
    private GridDataSource notes;

    @Property
    private Note note;

    @Inject
    private NoteDao noteDao;

    @Inject
    private DocumentDao documentDao;

    @Property
    private SqlPrimaryKeyEncoder<Note> encoder;

    @Inject
    @Path("NoteSearch.css")
    private Asset stylesheet;

    @Environmental
    private JavaScriptSupport support;

    private Collection<Note> selectedNotes;

    private Collection<Long> orphanNoteIds;

    private boolean removeSelected;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    @Property
    private boolean slsMode;

    @AfterRender
    void addStylesheet() {
        // This is needed to have the page specific style sheet after
        // other css includes
        // support.addStylesheetLink(stylesheet, null);
        support.importStylesheet(stylesheet);
    }

    void onActionFromCancel() {
        context = new Context(searchTerm);
    }

    void onActionFromToggleEdit() {
        context = new Context(searchTerm, "edit");
    }

    void onSelectedFromRemoveSelected() {
        removeSelected = true;
    }

    void onActivate(EventContext ctx) {
        // TODO : fetch only orpahNote ids for the elements displayed in this
        // window
        orphanNoteIds = noteDao.getOrphanIds();

        if (selectedNotes == null) {
            selectedNotes = new HashSet<Note>();
        }
        if (ctx.getCount() >= 1) {
            searchTerm = ctx.get(String.class, 0);
        }
        if (ctx.getCount() >= 2) {
            editMode = ctx.get(String.class, 1);
        }
        context = new Context(ctx);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onPrepare() {
        encoder = new SqlPrimaryKeyEncoder<Note>(noteDao);
    }

    void onSuccessFromEdit() {
        if (removeSelected) {
            noteDao.removeNotes(selectedNotes);

        } else {
            // getting all values from encoder
            for (Note editedNote : encoder.getAllValues()) {
                // If we get a not empty value for a field,
                // it means it has been edited
                // We must refetch the actual document note
                if (!isBlank(editedNote.getLemma())) {
                    Note currentNote = noteDao.getById(editedNote.getId());
                    currentNote.setLemma(editedNote.getLemma());
                    noteDao.save(editedNote);
                }
            }
        }

        context = new Context(searchTerm);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    void setupRender() {
        notes = noteDao.queryNotes(searchTerm == null ? "*" : searchTerm);
    }

    public boolean isNoteSelected() {
        return selectedNotes.contains(note);
    }

    public void setNoteSelected(boolean selected) {
        if (selected) {
            selectedNotes.add(note);
        } else {
            selectedNotes.remove(note);
        }
    }

    public boolean isNotesNotEmpty() {
        return notes.getAvailableRows() > 0;
    }

    public boolean isOrphanNote() {
        return orphanNoteIds.contains(note.getId());
    }

}
