/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.naming.event.EventContext;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.Term;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.TermDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
@Import(library = { "classpath:js/jquery-1.4.1.js", "deleteDialog.js" })
public class DictionarySearch {

    @Property
    private String searchTerm;

    private Context context;

    @InjectComponent
    private Grid termsGrid;

    @Property
    private GridDataSource terms;

    @Property
    private Note note;

    @Inject
    private NoteDao noteDao;

    @Inject
    private TermDao termDao;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Property
    private Term term;

    private Map<Note, Collection<DocumentNote>> documentNotes;

    void onActivate(EventContext ctx) {
        if (ctx.getCount() >= 1) {
            searchTerm = ctx.get(String.class, 0);
        }
        context = new Context(ctx);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    public void setupRender() {
        terms = noteDao.queryDictionary(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onActionFromDelete(long termId) {
        termDao.remove(termId);
    }

    private void initDocumentNotes() {
        // FIXME
        // // get notes from gridDataSource
        // List<Note> notes = new ArrayList<Note>();
        // int offset = (termsGrid.getCurrentPage()-1) *
        // termsGrid.getRowsPerPage();
        // for (int i = 0; i < termsGrid.getRowsPerPage(); i++){
        // TermWithNotes t = (TermWithNotes) terms.getRowValue(offset + i);
        // if (t != null){
        // notes.addAll(t.getNotes());
        // }
        // }
        //
        // // fetch document notes
        // List<DocumentNote> dn = documentNoteDao.getOfNotes(notes);
        //
        // // group document notes by note
        // documentNotes = new HashMap<Note, Collection<DocumentNote>>();
        // for (DocumentNote documentNote : dn){
        // Collection<DocumentNote> col =
        // documentNotes.get(documentNote.getNote());
        // if (col == null){
        // col = new HashSet<DocumentNote>();
        // documentNotes.put(documentNote.getNote(), col);
        // }
        // col.add(documentNote);
        // }
        documentNotes = new HashMap<Note, Collection<DocumentNote>>();
    }

    public String getFullSelections() {
        if (documentNotes == null) {
            initDocumentNotes();
        }

        if (documentNotes.containsKey(note)) {
            Collection<String> longTexts = new ArrayList<String>();
            for (DocumentNote documentNote : documentNotes.get(note)) {
                longTexts.add(documentNote.getFullSelection());
            }
            return StringUtils.join(longTexts, ", ");
        }
        return "";
    }
}
