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
import java.util.Set;

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
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.OrderBy;
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
@IncludeJavaScriptLibrary({ "classpath:jquery-1.4.1.js", "classpath:TapestryExt.js",
        "TextSelector.js", "AnnotatePage.js" })
@IncludeStylesheet("context:styles/tei.css")
public class AnnotatePage extends AbstractDocumentPage {

    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(AnnotatePage.class);

    @Property
    private NoteComment comment;

    @Property
    private Set<NoteComment> comments;

    @InjectComponent
    @Property
    private Zone commentZone;

    @Property
    private SelectedText createTermSelection;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    private List<DocumentNote> documentNotes;

    @Inject
    @Property
    private Block documentView;

    @Inject
    private Block emptyBlock;

    @Inject
    @Property
    private Block errorBlock;

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
    private DocumentNote note;

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

    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
    }

    public List<DocumentNote> getDocumentNotes() {
        if (documentNotes == null) {
            documentNotes = documentNoteRepository.query(getSearchInfo());
        }
        return documentNotes;
    }

    public Object[] getEditContext() {
        List<String> ctx = new ArrayList<String>(selectedNotes.size());
        for (DocumentNote r : selectedNotes) {
            if (!r.equals(note)) {
                ctx.add(r.getLocalId());
            }
        }
        return ctx.toArray();
    }

    private Term getEditTerm(Note n) {
        return n.getTerm() != null ? n.getTerm().createCopy() : new Term();
    }

    public String getNoteId() {
        return noteOnEdit != null ? noteOnEdit.getId() : null;
    }

    public DocumentNoteSearchInfo getSearchInfo() {
        if (searchInfo == null) {
            searchInfo = new DocumentNoteSearchInfo();
            searchInfo.getDocuments().add(getDocument());
        }
        return searchInfo;
    }

    void setupRender() {
        searchInfo = null;
    }

    void onActivate() {
        createTermSelection = new SelectedText();
        updateLongTextSelection = new SelectedText();
    }

    Object onDelete(EventContext context) throws IOException {
        noteOnEdit = documentNoteRepository.getById(context.get(String.class, 0));
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision = getDocumentRepo().removeNotes(documentRevision, noteOnEdit);

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
        selectedNotes = new ArrayList<DocumentNote>(context.getCount());
        for (int i = 0; i < context.getCount(); i++) {
            String localId = context.get(String.class, i);
            // XXX Where is this n coming?
            if (localId.startsWith("n")) {
                localId = localId.substring(1);
            }

            DocumentNote rev = documentNoteRepository.getByLocalId(getDocumentRevision(), localId);
            if (rev != null) {
                selectedNotes.add(rev);
            } else {
                logger.error("Note with localId " + localId + " coundn't be found in "
                        + getDocumentRevision());
            }
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

    Object onSuccessFromCommentForm() throws IOException {
        comments = noteOnEdit.getNote().getComments();
        if (newCommentMessage != null) {
            comments.add(noteRepository.createComment(noteOnEdit.getNote(), newCommentMessage));
            newCommentMessage = null;
        }

        return commentZone.getBody();
    }

    Object onSuccessFromCreateTerm() throws IOException {
        logger.info(createTermSelection.toString());
        DocumentRevision documentRevision = getDocumentRevision();

        DocumentNote documentNote = null;
        try {
            documentNote = getDocumentRepo().addNote(documentRevision, createTermSelection);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // prepare view (with new revision)
        documentRevision.setRevision(documentNote.getSVNRevision());
        selectedNotes = Collections.singletonList(documentNote);
        noteOnEdit = documentNote;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    public void setDocumentNotes(List<DocumentNote> documentNotes) {
        this.documentNotes = documentNotes;
    }

    public void setSearchInfo(DocumentNoteSearchInfo searchInfo) {
        this.searchInfo = searchInfo;
    }

}
