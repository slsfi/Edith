/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.TermWithNotes;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.TermDao;

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
    private TermWithNotes term;

    @Property
    private Note note;

    @Inject
    private NoteDao noteDao;

    @Inject
    private TermDao termDao;

    @Inject
    private DocumentNoteDao documentNoteDao;

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

    private void initDocumentNotes(){
        //TODO FIX THIS
//        // get notes from gridDataSource
//        List<Note> notes = new ArrayList<Note>();
//        int offset = (termsGrid.getCurrentPage()-1) * termsGrid.getRowsPerPage();
//        for (int i = 0; i < termsGrid.getRowsPerPage(); i++){
//            TermWithNotes t = (TermWithNotes) terms.getRowValue(offset + i);
//            if (t != null){
//                notes.addAll(t.getNotes());
//            }
//        }
//
//        // fetch document notes
//        List<DocumentNote> dn = documentNoteDao.getOfNotes(notes);
//
//        // group document notes by note
//        documentNotes = new HashMap<Note, Collection<DocumentNote>>();
//        for (DocumentNote documentNote : dn){
//            Collection<DocumentNote> col = documentNotes.get(documentNote.getNote());
//            if (col == null){
//                col = new HashSet<DocumentNote>();
//                documentNotes.put(documentNote.getNote(), col);
//            }
//            col.add(documentNote);
//        }
    }

    public String getLongTexts() {
        if (documentNotes == null){
            initDocumentNotes();
        }

        if (documentNotes.containsKey(note)){
            Collection<String> longTexts = new ArrayList<String>();
            for (DocumentNote documentNote : documentNotes.get(note)) {
                longTexts.add(documentNote.getFullSelection());
            }
            return StringUtils.join(longTexts, ", ");
        }else{
            return "";
        }

    }
}
