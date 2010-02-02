/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
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

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

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
    private NoteRevision note;

    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

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
        Document document = getDocument();
        DocumentRevision documentRevision = getDocumentRevision();
        docNotes = noteRevisionRepo.getOfDocument(document, documentRevision.getRevision());
    }

    Object onEdit(EventContext context) {
        Document document = getDocument();
        DocumentRevision documentRevision = getDocumentRevision();
        selectedNotes = new ArrayList<NoteRevision>(context.getCount());
        for (int i = 0; i < context.getCount(); i++) {
            String localId = context.get(String.class, i);
            //XXX Where is this n coming?
            if (localId.startsWith("n"))
                localId = localId.substring(1);
            
            NoteRevision rev = noteRevisionRepo.getByLocalId(document, documentRevision
                    .getRevision(), localId);
            if (rev != null) {
                selectedNotes.add(rev);
            }
        }
        
        if (selectedNotes.size() > 0 ) {
            noteOnEdit = selectedNotes.get(0);
        }
        
        //Order on lemma after we have selected the first one as
        //a selection
        Collections.sort(selectedNotes, new Comparator<NoteRevision>() {
            public int compare(NoteRevision o1, NoteRevision o2) {
                return o1.getLemma().compareTo(o2.getLemma());
            }
        });
        
        moreThanOneSelectable = selectedNotes.size() > 1;
            
        
        return noteEdit;
    }

    Object onSuccessFromCreateTerm() throws IOException {
        System.out.println(createTermSelection.startId + "," + createTermSelection.endId + ":["
                + createTermSelection.selection + "]");
        Document document = getDocument();
        DocumentRevision documentRevision = getDocumentRevision();
        Note note = getDocumentRepo().addNote(document, documentRevision.getRevision(),
                createTermSelection.startId, createTermSelection.endId,
                createTermSelection.selection);
        long newRevision = note.getLatestRevision().getSvnRevision();

        // update notesList content
        documentRevision.setRevision(newRevision);
        docNotes = noteRevisionRepo.getOfDocument(document, documentRevision.getRevision());
        return new MultiZoneUpdate("listZone", notesList).add("documentZone", documentView);
    }
    
    void onPrepareForSubmit(String noteRev) {
        note = noteRevisionRepo.getById(noteRev).createCopy();
        noteOnEdit = note;
    }
    
    Object onSuccessFromNoteEditForm() throws IOException {

        Document document = getDocument();

        if (updateLongTextSelection.hasSelection()) {
            note = getDocumentRepo().updateNote(document, note, updateLongTextSelection.startId,
                    updateLongTextSelection.endId, updateLongTextSelection.selection);
        } else {
            noteRevisionRepo.save(note);
        }
        
        // notesList content
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision.setRevision(note.getSvnRevision());
        docNotes = noteRevisionRepo.getOfDocument(document, documentRevision.getRevision());

        selectedNotes = Collections.singletonList(note);
        noteOnEdit = note;

        return new MultiZoneUpdate("editZone", noteEdit).add("listZone", notesList).add(
                "documentZone", documentView);
    }

    Object onActionFromDelete() throws IOException {

        // FIXME Do actual deletion

        Document document = getDocument();

        // notesList content
        DocumentRevision documentRevision = getDocumentRevision();
        docNotes = noteRevisionRepo.getOfDocument(document, documentRevision.getRevision());
        
        selectedNotes = Collections.emptyList();

        return new MultiZoneUpdate("editZone", emptyBlock).add("listZone", notesList).add(
                "documentZone", documentView);
    }
    
    private String localId(NoteRevision noteRev) {
        return noteRev.getRevisionOf().getLocalId(); 
    }
    
    
    public Object[] getEditContext() {
        List<String> ctx = new ArrayList<String>(selectedNotes.size());
        //Adding the current note to head
        ctx.add(localId(note));
        for(NoteRevision r : selectedNotes) {
            if (!r.equals(note)) {
                ctx.add(localId(r));
            }
        }
        return ctx.toArray();
    }
        
    public static class SelectedText {
        private String selection;
        private String startId;
        private String endId;

        public String getSelection() {
            return selection;
        }

        public void setSelection(String selection) {
            this.selection = selection;
        }

        public String getStartId() {
            return startId;
        }

        public void setStartId(String startId) {
            this.startId = startId;
        }

        public String getEndId() {
            return endId;
        }

        public void setEndId(String endId) {
            this.endId = endId;
        }

        public boolean hasSelection() {
            return selection != null && startId != null && endId != null
                    && selection.trim().length() > 0 && startId.trim().length() > 0
                    && endId.trim().length() > 0;
        }
    }
    
}
