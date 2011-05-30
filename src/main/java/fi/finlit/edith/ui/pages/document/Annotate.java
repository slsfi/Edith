/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.ui.components.InfoMessage;
import fi.finlit.edith.ui.components.note.Comments;
import fi.finlit.edith.ui.components.note.DocumentNotes;
import fi.finlit.edith.ui.components.note.NoteEdit;
import fi.finlit.edith.ui.components.note.SearchResults;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.TermRepository;
import fi.finlit.edith.ui.services.TimeService;

@Import(library = {
        "classpath:js/jquery-1.5.1.min.js", "classpath:js/TapestryExt.js",
        "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/jquery.dynatree.min.js",
        "TextSelector.js", "Annotate.js", "classpath:js/jqModal.js", "classpath:js/jquery.cookie.js" },
        stylesheet= {"context:styles/tei.css", 
        "context:styles/smoothness/jquery-ui-1.8.12.custom.css",
        "context:styles/dynatree/skin/ui.dynatree.css"})
@SuppressWarnings("unused")
public class Annotate extends AbstractDocumentPage {
    
    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(Annotate.class);

    @SessionState(create = false)
    private Collection<Document> selectedDocuments;

    @Property
    @Persist
    private SelectedText createTermSelection;

    @Inject
    private TimeService timeService;

    @Inject
    private DocumentNoteRepository documentNoteRepository;
  
    @Inject
    private Block documentView;

    @Inject
    private Block emptyBlock;

    @Inject
    @Property
    private Block closeDialog;

    @InjectComponent
    private InfoMessage infoMessage;
    
    @Inject
    private Messages messages;

    @Property
    private boolean moreThanOneSelectable;

    
    @Property
    private DocumentNote note;

    @Inject
    private NoteRepository noteRepository;

    @Property
    private String noteRevisionId;
    
    @Property
    private String selectedNoteLocalId;

    @Inject
    @Property
    private Block notesForLemma;

    @Inject
    private JavaScriptSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Persist
    private DocumentNoteSearchInfo searchInfo;

    @Inject
    private TermRepository termRepository;

    @Property
    private NoteType type;

    @Property
    private List<Note> notes;

    @Property
    private String personId;
    
    @Property
    private String noteToLinkId;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean slsMode;
    
    @InjectComponent
    private SearchResults searchResults;
    
    @InjectComponent
    private DocumentNotes documentNotes;
    
    @InjectComponent
    private NoteEdit noteEdit;
   
    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

   

    private Term getEditTerm(Note n) {
        return n.getTerm() != null ? n.getTerm() : new Term();
    }

    

    public DocumentNoteSearchInfo getSearchInfo() {
        if (searchInfo == null) {
            searchInfo = new DocumentNoteSearchInfo();
            searchInfo.getDocuments().add(getDocument());
            searchInfo.setCurrentDocument(getDocument());
        }
        if (selectedDocuments != null) {
            searchInfo.getDocuments().addAll(selectedDocuments);
        }
        return searchInfo;
    }

    void setupRender() {
        searchInfo = null;
    }

    void onActivate() {
        System.err.println("onActivate");
        if (createTermSelection == null) {
            createTermSelection = new SelectedText();
        }
    }
    
    Object onSuccessFromSelectNoteForm() {

        //Strip first n from the id
        String localId = selectedNoteLocalId.substring(1);
        DocumentNote documentNote = documentNoteRepository.getByLocalId(getDocumentRevision(),
                localId);
        documentNotes.setNoteId(documentNote.getNote().getId());
        documentNotes.setSelectedNote(documentNote);
        noteEdit.setDocumentNoteOnEdit(documentNote);
        return new MultiZoneUpdate("documentNotesZone", documentNotes.getBlock())
            .add("noteEditZone", noteEdit.getBlock());

    }

    Object onDelete(EventContext context) {
//        noteOnEdit = documentNoteRepository.getById(context.get(String.class, 0));
//        DocumentRevision documentRevision = getDocumentRevision();
//        documentRevision = getDocumentRepository().removeNotes(documentRevision, noteOnEdit);

        // prepare view with new revision
//        getDocumentRevision().setRevision(documentRevision.getRevision());
//        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate(EDIT_ZONE, emptyBlock)
                //.add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", emptyBlock);
    }

    Object onEdit(EventContext context) {
//        System.err.println("onEdit");
//        selectedNotes = new ArrayList<DocumentNote>();
//        if (context.getCount() > 0 && context.get(String.class, 0).startsWith("n")) {
//            for (int i = 0; i < context.getCount(); i++) {
//                String localId = context.get(String.class, i).substring(1);
//                DocumentNote docNote = documentNoteRepository.getByLocalId(getDocumentRevision(), localId);
//                if (docNote != null){
//                    selectedNotes.add(docNote);
//                }else{
//                    throw new IllegalStateException("No DocumentNote available for local id " + localId);
//                }
//            }
//        } else {
//            String localId = context.get(String.class, 0).substring(1);
//            DocumentNote docNote = documentNoteRepository.getByLocalId(getDocumentRevision(), localId);
//            if (docNote == null) {
//                docNote = new DocumentNote();
//                docNote.setLocalId(String.valueOf(timeService.currentTimeMillis()));
//                docNote.setNote(noteRepository.getById(context.get(String.class, 1)));
//            }
//            selectedNotes.add(docNote);
//        }
//
//        if (selectedNotes.size() > 0) {
//            noteOnEdit = selectedNotes.get(0);
//            termOnEdit = getEditTerm(noteOnEdit.getNote());
//            comments = getSortedComments(noteOnEdit.getConcept(slsMode).getComments());
//        } else {
//            comments = Collections.<NoteComment> emptyList();
//        }
//        moreThanOneSelectable = selectedNotes.size() > 1;
//        System.err.println("onEdit --");
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit)
//        .add("commentZone", commentZone.getBody())
        ;
    }
    
    private MultiZoneUpdate noteHasChanged(DocumentNote documentNote, String msg) {
        getDocumentRevision().setRevision(documentNote.getSVNRevision());
        
        documentNotes.setNoteId(documentNote.getNote().getId());
        documentNotes.setSelectedNote(documentNote);
        noteEdit.setDocumentNoteOnEdit(documentNote);
        infoMessage.addInfoMsg(msg);
        return zoneWithInfo(msg)
                .add("listZone", searchResults.getBlock())
                .add("documentNotesZone", documentNotes.getBlock())
                .add("noteEditZone", noteEdit.getBlock()).add("documentZone", documentView);
    }

    Object onSuccessFromConnectTermForm() {
        logger.info("connect term with note id " + noteToLinkId);

        try {
            Note note = noteRepository.getById(noteToLinkId);
            DocumentNote documentNote = noteRepository.createDocumentNote(note,
                    getDocumentRevision(), createTermSelection.getSelection());
            documentNote = getDocumentRepository().updateNote(documentNote, createTermSelection);

            return noteHasChanged(documentNote, "note-connect-success");

        } catch (Exception e) {
            return zoneWithError("note-connect-failed", e);
        }
    }

    Object onSuccessFromCreateTermForm() {
        logger.info(createTermSelection.toString());
        DocumentRevision documentRevision = getDocumentRevision();

//        notes = noteRepository.findNotes(Note.createLemmaFromLongText(createTermSelection
//                .getSelection()));
//        if (notes.isEmpty()) {
            DocumentNote documentNote = null;
            try {
                Note n = createNote();
                documentNote = getDocumentRepository().addNote(n, documentRevision,
                        createTermSelection);
            } catch (Exception e) {
                return zoneWithError("note-addition-failed", e);
            }
            
            return noteHasChanged(documentNote, "create-success");
                
                
                
                
                //            selectedNotes = Collections.singletonList(documentNote);
//            termOnEdit = getEditTerm(noteOnEdit.getNote());
//            return new MultiZoneUpdate(EDIT_ZONE, noteEdit)
//                    //.add("listZone", notesList)
//                    .add("documentZone", documentView).add("commentZone", commentZone.getBody())
//                    .add("dialogZone", closeDialog);
//        }
        //return new MultiZoneUpdate("dialogZone", notesForLemma);
    }
            
    private Note createNote() {
        Note n = new Note();
        if (slsMode) {
            n.setTerm(new Term());
        }
        return n;
}
            

    public void setSearchInfo(DocumentNoteSearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    Object onChooseBackingNote() {
        return handleUserChoice(null);
    }

    Object onChooseBackingNote(String noteId) {
        return handleUserChoice(noteId);
    }

    private Object handleUserChoice(String noteId) {
//        logger.info(createTermSelection.toString());
//        DocumentNote documentNote;
//        DocumentRevision documentRevision = getDocumentRevision();
//        try {
//            if (noteId == null) {
//                documentNote = getDocumentRepository().addNote(createNote(), documentRevision,
//                        createTermSelection);
//            } else {
//                documentNote = getDocumentRepository().addNote(noteRepository.getById(noteId),
//                        documentRevision, createTermSelection);
//            }
//        } catch (Exception e) {
//            logger.error(e.getMessage(), e);
//            infoMessage = messages.format("note-addition-failed");
//            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
//        }
//        documentRevision.setRevision(documentNote.getSVNRevision());
//        selectedNotes = Collections.singletonList(documentNote);
//        noteOnEdit = documentNote;
//        termOnEdit = getEditTerm(noteOnEdit.getNote());
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit)
            //.add("listZone", notesList)
                //.add("documentZone", documentView).add("commentZone", commentZone.getBody())
                .add("dialogZone", closeDialog);
    }

    public boolean isSlsMode() {
        return slsMode;
    }
    
    public NoteEdit getNoteEdit() {
        return noteEdit;
    }
    
    public InfoMessage getInfoMessage() {
        return infoMessage;
    }
    
    public SearchResults getSearchResults() {
        return searchResults;
    }
    
    public DocumentNotes getDocumentNotes() {
        return documentNotes;
    }
    
    public Block getDocumentView() {
        return documentView;
    }
    
    public MultiZoneUpdate zoneWithInfo(String msg) {
        getInfoMessage().addInfoMsg(msg);
        return new MultiZoneUpdate("infoMessageZone", getInfoMessage().getBlock());
    }
    
    public MultiZoneUpdate zoneWithError(String msg, Throwable e) {
        logger.error(msg, e);
        getInfoMessage().addErrorMsg(msg);
        return new MultiZoneUpdate("infoMessageZone", getInfoMessage().getBlock());
    }
    
}
