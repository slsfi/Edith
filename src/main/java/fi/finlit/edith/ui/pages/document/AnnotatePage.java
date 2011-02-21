/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.AfterRender;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SessionState;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.NoteWithInstances;
import fi.finlit.edith.ui.services.TermRepository;
import fi.finlit.edith.ui.services.TimeService;

@IncludeJavaScriptLibrary({ "classpath:jquery-1.4.1.js", "classpath:TapestryExt.js",
        "TextSelector.js", "AnnotatePage.js", "classpath:jqModal.js" })
@IncludeStylesheet("context:styles/tei.css")
@SuppressWarnings("unused")
public class AnnotatePage extends AbstractDocumentPage {

    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(AnnotatePage.class);

    @SessionState(create = false)
    private Collection<Document> selectedDocuments;

    @Property
    private NoteComment comment;

    @Property
    private Set<NoteComment> comments;

    @InjectComponent
    @Property
    private Zone commentZone;

    @Property
    @Persist
    private SelectedText createTermSelection;

    @Inject
    private TimeService timeService;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private List<NoteWithInstances> notesWithInstances;

    @Inject
    @Property
    private Block documentView;

    @Inject
    private Block emptyBlock;

    @Inject
    @Property
    private Block errorBlock;

    @Inject
    @Property
    private Block closeDialog;

    @Inject
    @Property
    private Block personForm;

    @Inject
    @Property
    private Block placeForm;

    @Inject
    private Block infoBlock;

    @Property
    private String infoMessage;

    @Inject
    private Messages messages;

    @Property
    private boolean moreThanOneSelectable;

    @Property
    private String newCommentMessage;

    @Property
    private NoteWithInstances noteWithInstances;

    @Inject
    @Property
    private Block noteEdit;

    @Property
    private DocumentNote noteOnEdit;

    @Inject
    private NoteRepository noteRepository;

    @Property
    private String noteRevisionId;

    @Inject
    @Property
    private Block notesList;

    @Inject
    @Property
    private Block notesForLemma;

    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Persist
    private DocumentNoteSearchInfo searchInfo;

    @Property
    private List<DocumentNote> selectedNotes;

    @Property
    private Term termOnEdit;

    @Inject
    private TermRepository termRepository;

    @Property
    private NoteType type;

    @Property
    private SelectedText updateLongTextSelection;

    @Property
    private List<Note> notes;

    @Property
    private Note loopNote;

    @Property
    private String personId;

    @Property
    private DocumentNote note;

    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

    public List<NoteWithInstances> getDocumentNotes() {
        if (notesWithInstances == null) {
            notesWithInstances = noteRepository.query(getSearchInfo());
        }
        return notesWithInstances;
    }

    private Term getEditTerm(Note n) {
        return n.getTerm() != null ? n.getTerm() : new Term();
    }

    public String getNoteId() {
        return noteOnEdit != null ? noteOnEdit.getId() : null;
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
        if (createTermSelection == null) {
            createTermSelection = new SelectedText();
        }
        if (updateLongTextSelection == null) {
            updateLongTextSelection = new SelectedText();
        }
    }

    Object onDelete(EventContext context) {
        noteOnEdit = documentNoteRepository.getById(context.get(String.class, 0));
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision = getDocumentRepository().removeNotes(documentRevision, noteOnEdit);

        // prepare view with new revision
        getDocumentRevision().setRevision(documentRevision.getRevision());
        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate(EDIT_ZONE, emptyBlock).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", emptyBlock);
    }

    Object onDeleteComment(String noteId, String commentId) {
        NoteComment deletedComment = noteRepository.removeComment(commentId);
        noteOnEdit = documentNoteRepository.getById(noteId);
        comments = noteOnEdit.getNote().getComments();
        comments.remove(deletedComment);
        return commentZone.getBody();
    }

    Object onEdit(EventContext context) {
        selectedNotes = new ArrayList<DocumentNote>();
        if (context.getCount() > 0 && context.get(String.class, 0).startsWith("n")) {
            for (int i = 0; i < context.getCount(); i++) {
                String localId = context.get(String.class, i).substring(1);
                DocumentNote docNote = documentNoteRepository.getByLocalId(getDocumentRevision(), localId);
                if (docNote != null){
                    selectedNotes.add(docNote);
                }else{
                    throw new IllegalStateException("No DocumentNote available for local id " + localId);
                }
            }
        } else {
            String localId = context.get(String.class, 0).substring(1);
            DocumentNote docNote = documentNoteRepository.getByLocalId(getDocumentRevision(), localId);
            if (docNote == null) {
                docNote = new DocumentNote();
                docNote.setLocalId(String.valueOf(timeService.currentTimeMillis()));
                docNote.setNote(noteRepository.getById(context.get(String.class, 1)));
            }
            selectedNotes.add(docNote);
        }

        if (selectedNotes.size() > 0) {
            noteOnEdit = selectedNotes.get(0);
            termOnEdit = getEditTerm(noteOnEdit.getNote());
            comments = noteOnEdit.getNote().getComments();
        } else {
            comments = Collections.<NoteComment> emptySet();
        }
        moreThanOneSelectable = selectedNotes.size() > 1;
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("commentZone", commentZone.getBody());
    }

    void onPrepareFromCommentForm(String id) {
        noteOnEdit = documentNoteRepository.getById(id);
    }

    Object onSuccessFromCommentForm() {
        comments = noteOnEdit.getNote().getComments();
        if (newCommentMessage != null) {
            comments.add(noteRepository.createComment(noteOnEdit.getNote(), newCommentMessage));
            newCommentMessage = null;
        }

        return commentZone.getBody();
    }

    Object onSuccessFromCreateTerm() {
        logger.info(createTermSelection.toString());
        DocumentRevision documentRevision = getDocumentRevision();

        notes = noteRepository.findNotes(Note.createLemmaFromLongText(createTermSelection
                .getSelection()));
        if (notes.isEmpty()) {
            DocumentNote documentNote = null;
            try {
                Note n = new Note();
                documentNote = getDocumentRepository().addNote(n, documentRevision,
                        createTermSelection);
            } catch (Exception e) {
                logger.error(e.getMessage(), e);
                infoMessage = messages.format("note-addition-failed");
                return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
            }
            documentRevision.setRevision(documentNote.getSVNRevision());
            selectedNotes = Collections.singletonList(documentNote);
            noteOnEdit = documentNote;
            termOnEdit = getEditTerm(noteOnEdit.getNote());
            return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                    .add("documentZone", documentView).add("commentZone", commentZone.getBody())
                    .add("dialogZone", closeDialog);
        }
        return new MultiZoneUpdate("dialogZone", notesForLemma);
    }

    public void setSearchInfo(DocumentNoteSearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

    public String getTypesString() {
        Collection<String> translated = new ArrayList<String>();
        for (NoteType t : noteWithInstances.getNote().getTypes()) {
            translated.add(messages.get(t.toString()));
        }
        return StringUtils.join(translated, ", ");
    }

    public String getEditorsForNote() {
        return getEditors(note);
    }

    public String getEditorsForNoteOnEdit() {
        return getEditors(noteOnEdit);
    }

    private String getEditors(DocumentNote documentNote) {
        Collection<String> result = new ArrayList<String>();
        for (UserInfo user : documentNote.getNote().getAllEditors()) {
            if (!documentNote.getNote().getLastEditedBy().equals(user)) {
                result.add(user.getUsername());
            }
        }
        return StringUtils.join(result, ", ");
    }

    public int getLemmaInstances() {
        return documentNoteRepository.getOfNote(noteOnEdit.getNote().getId()).size();
    }

    public int getNumberOfInstancesInDocument() {
        return noteWithInstances.getDocumentNotes().size();
    }

    Object onChooseBackingNote() {
        return handleUserChoice(null);
    }

    Object onChooseBackingNote(String noteId) {
        return handleUserChoice(noteId);
    }

    private Object handleUserChoice(String noteId) {
        logger.info(createTermSelection.toString());
        DocumentNote documentNote;
        DocumentRevision documentRevision = getDocumentRevision();
        try {
            if (noteId == null) {
                documentNote = getDocumentRepository().addNote(new Note(), documentRevision,
                        createTermSelection);
            } else {
                documentNote = getDocumentRepository().addNote(noteRepository.getById(noteId),
                        documentRevision, createTermSelection);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }
        documentRevision.setRevision(documentNote.getSVNRevision());
        selectedNotes = Collections.singletonList(documentNote);
        noteOnEdit = documentNote;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", commentZone.getBody())
                .add("dialogZone", closeDialog);
    }

    Object onCreatePerson() {
        return personForm;
    }

    Object onCreatePlace() {
        return placeForm;
    }

    public DocumentNoteType getDocumentNoteType() {
        if (note.getDocument() == null) {
            return DocumentNoteType.ORPHAN;
        } else if (note.getLongText() == null) {
            return DocumentNoteType.SEMI_ORPHAN;
        } else if (!note.getDocument().equals(getDocument())) {
            return DocumentNoteType.ELSEWHERE;
        } else {
            return DocumentNoteType.NORMAL;
        }
    }

    private enum DocumentNoteType {
        NORMAL, SEMI_ORPHAN, ORPHAN, ELSEWHERE;
    }
}
