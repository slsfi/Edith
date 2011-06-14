/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.Collection;
import java.util.HashSet;

import org.apache.tapestry5.Asset;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Path;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.PrimaryKeyEncoder;

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
    private NoteDao noteRepository;

    @Inject
    private DocumentRepository documentRepository;

    @Property
    private PrimaryKeyEncoder<Note> encoder;

    @Inject
    @Path("NoteSearch.css")
    private Asset stylesheet;

    @Environmental
    private JavaScriptSupport support;

    private Collection<Note> selectedNotes;

    private Collection<String> orphanNoteIds;

    private boolean removeSelected;
    
    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    @Property
    private boolean slsMode;

    @AfterRender
    void addStylesheet() {
        // This is needed to have the page specific style sheet after
        // other css includes
//        support.addStylesheetLink(stylesheet, null);
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
        // TODO : fetch only orpahNote ids for the elements displayed in this window
        orphanNoteIds = noteRepository.getOrphanIds();

        if (selectedNotes == null){
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
        encoder = new PrimaryKeyEncoder<Note>(noteRepository);
    }

    void onSuccessFromEdit() {
        if (removeSelected){
            noteRepository.removeNotes(selectedNotes);

        }else{
          //getting all values from encoder
            for(Note editedNote : encoder.getAllValues() ){
              //If we get a not empty value for a field,
              //it means it has been edited
              //We must refetch the actual document note
              if (!isBlank(editedNote.getLemma())) {
                  Note currentNote = noteRepository.getById(editedNote.getId());
                  currentNote.setLemma(editedNote.getLemma());
                  noteRepository.save(editedNote);
              }
            }
        }

        context = new Context(searchTerm);
    }

    void onSuccessFromSearch() {
        context = new Context(searchTerm);
    }

    void setupRender() {
        notes = noteRepository.queryNotes(searchTerm == null ? "*" : searchTerm);
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

    public boolean isNotesNotEmpty(){
        return notes.getAvailableRows() > 0;
    }

    public boolean isOrphanNote(){
        return orphanNoteIds.contains(note.getId());
    }

    public Concept getConcept() {
        return note.getConcept(slsMode);
    }
}
