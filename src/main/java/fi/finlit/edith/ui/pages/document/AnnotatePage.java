/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermRepository;

/**
 * AnnotatePage provides
 * 
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.3.2.js", "classpath:TapestryExt.js",
        "AnnotatePage.js" })
@IncludeStylesheet("context:styles/tei.css")
public class AnnotatePage extends AbstractDocumentPage {

    private static final Logger logger = LoggerFactory.getLogger(AnnotatePage.class);
    
    @Inject
    @Property
    private Block notesList;
    
    @Inject
    private Block noteEdit;
    
    @Inject
    private Block emptyBlock;

    @Inject
    @Property
    private Block documentView;

    @Property
    private List<NoteRevision> selectedNotes;
    
    @Property 
    private NoteRevision noteOnEdit;
    
    @Property
    private Term termOnEdit;

    @Property
    private NoteRevision note;

    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject
    private NoteRepository noteRepo;

    @Inject
    private TermRepository termRepo;
    
    @Property
    private List<NoteRevision> docNotes;

    @Property
    private SelectedText createTermSelection;
    
    @Property
    private SelectedText updateLongTextSelection;
    
    @Property
    private boolean moreThanOneSelectable;
    
    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

    void onActivate() {
        createTermSelection = new SelectedText();
        updateLongTextSelection = new SelectedText();
    }

    void setupRender() {
        docNotes = noteRevisionRepo.getOfDocument(getDocumentRevision());
    }

    Object onEdit(EventContext context) {
        selectedNotes = new ArrayList<NoteRevision>(context.getCount());
        for (int i = 0; i < context.getCount(); i++) {
            String localId = context.get(String.class, i);
            //XXX Where is this n coming?
            if (localId.startsWith("n")){
                localId = localId.substring(1);
            }               
            
            NoteRevision rev = noteRevisionRepo.getByLocalId(getDocumentRevision(), localId);
            if (rev != null) {
                selectedNotes.add(rev);
            }else{
                logger.error("Note with localId " + localId + " coundn't be found in " + getDocumentRevision());
            }
        }
        
        if (selectedNotes.size() > 0 ) {
            noteOnEdit = selectedNotes.get(0);
            termOnEdit = getEditTerm(noteOnEdit);
        }
        
        //Order on lemma after we have selected the first one as a selection
        Collections.sort(selectedNotes, new Comparator<NoteRevision>() {
            public int compare(NoteRevision o1, NoteRevision o2) {
                return o1.getLemma().compareTo(o2.getLemma());
            }
        });
        
        moreThanOneSelectable = selectedNotes.size() > 1;
            
        
        return noteEdit;
    }

    private Term getEditTerm(NoteRevision noteRevision) {
        return noteRevision.getRevisionOf().getTerm() != null ? noteRevision.getRevisionOf()
                .getTerm().createCopy() : new Term();
    }

    Object onSuccessFromCreateTerm() throws IOException {
        System.out.println(createTermSelection);
        DocumentRevision documentRevision = getDocumentRevision();
        NoteRevision noteRevision = getDocumentRepo().addNote(documentRevision, createTermSelection);

        // prepare view (with new revision)
        documentRevision.setRevision(noteRevision.getSvnRevision());
        docNotes = noteRevisionRepo.getOfDocument(documentRevision);
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit);
        return new MultiZoneUpdate("editZone", noteEdit).add("listZone", notesList).add("documentZone", documentView);
    }
    
    void onPrepareForSubmit(String noteRev) {
        note = noteRevisionRepo.getById(noteRev).createCopy();
        noteOnEdit = note;
        termOnEdit = getEditTerm(noteOnEdit);
    }
    
    Object onSuccessFromNoteEditForm() throws IOException {
	NoteRevision noteRevision;
      
        if (updateLongTextSelection.hasSelection()) {
            noteRevision = getDocumentRepo().updateNote(note, updateLongTextSelection);
        } else {
            noteRevision = noteRevisionRepo.save(note);
        }
        
        //XXX This doesn't look good
        if (termOnEdit.getBasicForm() != null && !termOnEdit.getBasicForm().trim().isEmpty()) {
            Term term = termRepo.findByBasicForm(termOnEdit.getBasicForm());
            if (term == null) {
                term = termOnEdit;
                termRepo.save(term);
            }

            noteRevision.getRevisionOf().setTerm(term);
            noteRepo.save(noteRevision.getRevisionOf());
        }
        
        // prepare view (with possibly new revision)
        getDocumentRevision().setRevision(noteRevision.getSvnRevision());
        docNotes = noteRevisionRepo.getOfDocument(noteRevision.getDocumentRevision());
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit);

        return new MultiZoneUpdate("editZone", noteEdit).add("listZone", notesList).add(
                "documentZone", documentView);
    }

    Object onDelete(EventContext context) throws IOException {
        note = noteRevisionRepo.getById(context.get(String.class, 0));
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision = getDocumentRepo().removeNotes(documentRevision, note.getRevisionOf());

        // prepare view with new revision
        getDocumentRevision().setRevision(documentRevision.getRevision());
        docNotes = noteRevisionRepo.getOfDocument(documentRevision);        
        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate("editZone", emptyBlock).add("listZone", notesList).add("documentZone", documentView);
    }
    
    List<String> onProvideCompletionsFromBasicForm(String partial) {
        
        List<Term> terms = termRepo.findByStartOfBasicForm(partial, 10);
        
        List<String> matches = new ArrayList<String>(terms.size());
        for (Term term : terms) {
            matches.add(term.getBasicForm());
        }
        return matches;
    }

    
    public Object[] getEditContext() {
        List<String> ctx = new ArrayList<String>(selectedNotes.size());
        //Adding the current note to head
        ctx.add(note.getLocalId());
        for(NoteRevision r : selectedNotes) {
            if (!r.equals(note)) {
                ctx.add(r.getLocalId());
            }
        }
        return ctx.toArray();
    }
        
}
