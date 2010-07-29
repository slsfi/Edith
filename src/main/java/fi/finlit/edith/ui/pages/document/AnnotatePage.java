/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

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
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.ParagraphParser;

/**
 * AnnotatePage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "classpath:TapestryExt.js",
    "TextSelector.js", "AnnotatePage.js" })
    @IncludeStylesheet("context:styles/tei.css")
    public class AnnotatePage extends AbstractDocumentPage {

    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(AnnotatePage.class);

    @Inject
    @Property
    private Block notesList;

    @Inject
    @Property
    private Block noteEdit;

    @Inject
    private Block emptyBlock;

    @Inject
    private Block infoBlock;

    @Inject
    @Property
    private Block errorBlock;

    @Inject
    @Property
    private Block documentView;

    @InjectComponent
    @Property
    private Zone commentZone;
    
    @Inject
    private Messages messages;

    @Property
    private String infoMessage;
    
    @Property
    private List<DocumentNote> selectedNotes;

    @Property
    private List<DocumentNote> documentNotes;
    
    @Property
    private DocumentNote note;
    
    @Property
    private DocumentNote noteOnEdit;
    
    @Property
    private Term termOnEdit;
    
    @Inject
    private RenderSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private NoteRepository noteRepository;

    @Inject
    private TermRepository termRepo;

    @Property
    private SelectedText createTermSelection;

    @Property
    private SelectedText updateLongTextSelection;

    @Property
    private boolean moreThanOneSelectable;

    @Property
    private NoteComment comment;

    @Property
    private String newCommentMessage;

    @Property
    private String noteRevisionId;

    @Property
    private NoteType type;

    @Property
    private Set<NoteComment> comments;

    @AfterRender
    void addScript() {
        String link = resources.createEventLink("edit", "CONTEXT").toAbsoluteURI();
        renderSupport.addScript("editLink = '" + link + "';");
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

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note
                .getTerm().createCopy() : new Term();
    }
    
    public String getNoteId(){
        return noteOnEdit != null ? noteOnEdit.getId() : null;
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
        documentNotes = documentNoteRepository.getOfDocument(documentRevision);
        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate(EDIT_ZONE, emptyBlock)
            .add("listZone", notesList)
            .add("documentZone", documentView)
            .add("commentZone", emptyBlock);
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
        }

        // Order on lemma after we have selected the first one as a selection
        // FIXME
        //        Collections.sort(selectedNotes, new NoteComparator());

        moreThanOneSelectable = selectedNotes.size() > 1;
//        noteId = noteOnEdit.getNote().getId();
        comments = noteOnEdit.getNote().getComments();
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("commentZone", commentZone.getBody());
    }
    
    Object onDeleteComment(String commentId) {
        NoteComment deletedComment = noteRepository.removeComment(commentId);
        String noteId = deletedComment.getNote().getId();
        Note n = noteRepository.getById(noteId);
        comments = n.getComments();
        return commentZone.getBody();
    }

    void onPrepareFromCommentForm(String id) {
        noteOnEdit = documentNoteRepository.getById(id);
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepo.findByStartOfBasicForm(partial, 10);
    }

    Object onSuccessFromCommentForm() throws IOException {
//        Note n = noteRepository.getById(noteId);
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
        documentNotes = documentNoteRepository.getOfDocument(documentRevision);
        selectedNotes = Collections.singletonList(documentNote);
        noteOnEdit = documentNote;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
//        noteId = noteOnEdit.getNote().getId();
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit)
            .add("listZone", notesList)
            .add("documentZone", documentView)
            .add("commentZone", commentZone.getBody());
    }

    void setupRender() {
        documentNotes = documentNoteRepository.getOfDocument(getDocumentRevision());
    }

}
