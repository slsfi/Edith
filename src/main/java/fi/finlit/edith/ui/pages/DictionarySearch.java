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
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.TermWithNotes;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.TermRepository;

@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "deleteDialog.js" })
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
    private NoteRepository noteRepository;

    @Inject
    private TermRepository termRepository;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    private Map<Note, Collection<DocumentNote>> documentNotes;

    @Environmental
    private RenderSupport support;

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
        terms = noteRepository.queryDictionary(searchTerm == null ? "*" : searchTerm);
    }

    Object onPassivate() {
        return context == null ? null : context.toArray();
    }

    void onActionFromDelete(String termId) {
        termRepository.remove(termId);
    }

    private void initDocumentNotes(){
        // get notes from gridDataSource
        List<Note> notes = new ArrayList<Note>();
        int offset = (termsGrid.getCurrentPage()-1) * termsGrid.getRowsPerPage();
        for (int i = 0; i < termsGrid.getRowsPerPage(); i++){
            TermWithNotes t = (TermWithNotes) terms.getRowValue(offset + i);
            if (t != null){
                notes.addAll(t.getNotes());
            }
        }

        // fetch document notes
        List<DocumentNote> dn = documentNoteRepository.getOfNotes(notes);

        // group document notes by note
        documentNotes = new HashMap<Note, Collection<DocumentNote>>();
        for (DocumentNote documentNote : dn){
            Collection<DocumentNote> col = documentNotes.get(documentNote.getNote());
            if (col == null){
                col = new HashSet<DocumentNote>();
                documentNotes.put(documentNote.getNote(), col);
            }
            col.add(documentNote);
        }
    }

    public String getLongTexts() {
        if (documentNotes == null){
            initDocumentNotes();
        }

        if (documentNotes.containsKey(note)){
            Collection<String> longTexts = new ArrayList<String>();
            for (DocumentNote documentNote : documentNotes.get(note)) {
                longTexts.add(documentNote.getLongText());
            }
            return StringUtils.join(longTexts, ", ");
        }else{
            return "";
        }

    }
}
