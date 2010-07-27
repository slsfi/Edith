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
    private Block noteEdit;

    @InjectComponent
    private Zone commentZone;

    @Inject
    private Block emptyBlock;

    @Inject
    private Block infoBlock;

    @Inject
    private Block errorBlock;

    @Inject
    private Messages messages;

    @Property
    private String infoMessage;

    @Inject
    @Property
    private Block documentView;

    @Property
    private List<DocumentNote> selectedNotes;

    @Property
    private DocumentNote noteOnEdit;

    @Property
    private Term termOnEdit;

    @Property
    private DocumentNote note;

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
    private List<DocumentNote> documentNotes;

    @Property
    private SelectedText createTermSelection;

    @Property
    private SelectedText updateLongTextSelection;

    @Property
    private boolean moreThanOneSelectable;

    @Property
    private boolean submitSuccess;

    @Property
    private String noteId;

    @Property
    private NameForm loopPerson;

    @Property
    private NameForm loopPlace;

    @Property
    private String newPersonFirst;

    @Property
    private String newPersonLast;

    @Property
    private String newPersonDescription;

    @Property
    private String newPlaceName;

    @Property
    private String newPlaceDescription;

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

    public String getDescription() {
        if (noteOnEdit.getNote().getDescription() == null) {
            return null;
        }
        return noteOnEdit.getNote().getDescription().toString();
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

    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getNote().getFormat();
    }

    public TermLanguage getLanguage() {
        return termOnEdit.getLanguage();
    }

    public NameForm getNormalizedPerson() {
        if (noteOnEdit.getNote().getPerson() == null) {
            noteOnEdit.getNote().setPerson(new Person(new NameForm(), new HashSet<NameForm>()));
        }
        return noteOnEdit.getNote().getPerson().getNormalizedForm();
    }

    public NameForm getNormalizedPlace() {
        if (noteOnEdit.getNote().getPlace() == null) {
            noteOnEdit.getNote().setPlace(new Place(new NameForm(), new HashSet<NameForm>()));
        }
        return noteOnEdit.getNote().getPlace().getNormalizedForm();
    }


    public Set<NameForm> getPersons() {
        return noteOnEdit.getNote().getPerson().getOtherForms();
    }

    public Set<NameForm> getPlaces() {
        return noteOnEdit.getNote().getPlace().getOtherForms();
    }

    public Set<NoteType> getSelectedTypes() {
        if (noteOnEdit.getNote().getTypes() == null) {
            noteOnEdit.getNote().setTypes(new HashSet<NoteType>());
        }
        return noteOnEdit.getNote().getTypes();
    }

    public String getSources() {
        if (noteOnEdit.getNote().getSources() == null) {
            return null;
        }
        return noteOnEdit.getNote().getSources().toString();
    }

    public NoteStatus getStatus() {
        return noteOnEdit.getStatus();
    }

    public EnumSelectModel getStatusModel() {
        NoteStatus[] availableStatuses = noteOnEdit.getStatus().equals(
                NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL, NoteStatus.DRAFT,
            NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
                return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public String getTimeOfBirth() {
        return noteOnEdit.getNote().getPerson().getTimeOfBirth() == null ? null : noteOnEdit.getNote().getPerson()
                .getTimeOfBirth().asString();
    }

    public String getTimeOfDeath() {
        return noteOnEdit.getNote().getPerson().getTimeOfDeath() == null ? null : noteOnEdit.getNote().getPerson()
                .getTimeOfDeath().asString();
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }

    void onActivate() {
        createTermSelection = new SelectedText();
        updateLongTextSelection = new SelectedText();
    }

    Object onDelete(EventContext context) throws IOException {
        note = documentNoteRepository.getById(context.get(String.class, 0));
        DocumentRevision documentRevision = getDocumentRevision();
        documentRevision = getDocumentRepo().removeNotes(documentRevision, note);

        // prepare view with new revision
        getDocumentRevision().setRevision(documentRevision.getRevision());
        documentNotes = documentNoteRepository.getOfDocument(documentRevision);
        selectedNotes = Collections.emptyList();
        return new MultiZoneUpdate(EDIT_ZONE, emptyBlock).add("listZone", notesList).add(
                "documentZone", documentView).add("commentZone", emptyBlock);
    }

    Object onDeleteComment(String commentId) {
        NoteComment deletedComment = noteRepository.removeComment(commentId);
        noteId = deletedComment.getNote().getId();
        Note n = noteRepository.getById(noteId);
        comments = n.getComments();
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
        }

        // Order on lemma after we have selected the first one as a selection
        // FIXME
        //        Collections.sort(selectedNotes, new NoteComparator());

        moreThanOneSelectable = selectedNotes.size() > 1;
        noteId = noteOnEdit.getNote().getId();
        comments = noteOnEdit.getNote().getComments();
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("commentZone", commentZone.getBody());
    }

    void onPrepareFromCommentForm(String id) {
        noteId = id;
    }

    void onPrepareFromNoteEditForm(String noteRev) {
        note = documentNoteRepository.getById(noteRev).createCopy();
        noteOnEdit = note;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepo.findByStartOfBasicForm(partial, 10);
    }

    Object onSuccessFromCommentForm() throws IOException {
        Note n = noteRepository.getById(noteId);
        comments = n.getComments();
        if (newCommentMessage != null) {
            comments.add(noteRepository.createComment(n, newCommentMessage));
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
        noteId = noteOnEdit.getNote().getId();
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList).add(
                "documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    Object onSuccessFromNoteEditForm() throws IOException {
        DocumentNote noteRevision;
        if (note.getStatus().equals(NoteStatus.INITIAL)) {
            note.setStatus(NoteStatus.DRAFT);
        }
        updateNames(noteOnEdit.getNote().getPerson().getOtherForms(), newPersonFirst, newPersonLast,
                newPersonDescription);
        newPersonFirst = null;
        newPersonLast = null;
        newPersonDescription = null;
        updateName(noteOnEdit.getNote().getPlace().getOtherForms(), newPlaceName, newPlaceDescription);
        newPlaceName = null;
        newPlaceDescription = null;

        try {
            if (updateLongTextSelection.isValid()) {
                noteRevision = getDocumentRepo().updateNote(note, updateLongTextSelection);
            } else {
                noteRevision = documentNoteRepository.save(note);
            }
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // Handling the embedded term edit
        if (StringUtils.isNotBlank(termOnEdit.getBasicForm())) {
            saveTerm(noteRevision);
        }

        // prepare view (with possibly new revision)
        if (noteRevision.getSVNRevision() > getDocumentRevision().getRevision()) {
            getDocumentRevision().setRevision(noteRevision.getSVNRevision());
        }
        documentNotes = documentNoteRepository.getOfDocument(getDocumentRevision());
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        noteId = noteOnEdit.getNote().getId();
        submitSuccess = true;
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList).add(
                "documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    private void saveTerm(DocumentNote noteRevision) {
        // The idea is that language can be changed without a new term being created. It is a
        // bit hard to follow I admit. -vema
        List<Term> terms = termRepo.findByBasicForm(termOnEdit.getBasicForm());
        Term term = terms.isEmpty() ? termOnEdit : null;
        for (Term current : terms) {
            if (termOnEdit.getMeaning().equals(current.getMeaning())) {
                term = current;
                term.setLanguage(termOnEdit.getLanguage());
                break;
            }
        }
        if (term == null) {
            term = termOnEdit.createCopy();
        }
        termRepo.save(term);
        noteRevision.getNote().setTerm(term);
        noteRepository.save(noteRevision.getNote());
    }

    public void setDescription(String description) throws XMLStreamException {
        if (description != null) {
            noteOnEdit.getNote().setDescription(ParagraphParser.parseParagraph(description));
        }
    }

    public void setFormat(NoteFormat format) {
        noteOnEdit.getNote().setFormat(format);
    }

    @Validate("required")
    public void setLanguage(TermLanguage language) {
        termOnEdit.setLanguage(language);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            getSelectedTypes().add(type);
        } else {
            getSelectedTypes().remove(type);
        }
    }

    public void setSources(String sources) throws XMLStreamException {
        if (sources != null) {
            noteOnEdit.getNote().setSources(ParagraphParser.parseParagraph(sources));
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        note.setStatus(status);
    }

    public void setTimeOfBirth(String time) {
        if (time != null) {
            noteOnEdit.getNote().getPerson().setTimeOfBirth(Interval.fromString(time));
        }
    }

    public void setTimeOfDeath(String time) {
        if (time != null) {
            noteOnEdit.getNote().getPerson().setTimeOfDeath(Interval.fromString(time));
        }
    }

    void setupRender() {
        documentNotes = documentNoteRepository.getOfDocument(getDocumentRevision());
    }

    private void updateName(Set<NameForm> nameForms, String name, String description) {
        updateNames(nameForms, null, name, description);
    }

    private void updateNames(Set<NameForm> nameForms, String first, String last, String description) {
        if (last != null) {
            if (first == null) {
                nameForms.add(new NameForm(last, description));
            } else {
                nameForms.add(new NameForm(first, last, description));
            }
        }
        // Removes name forms that don't have a name entered.
        Iterator<NameForm> iter = nameForms.iterator();
        while (iter.hasNext()) {
            NameForm current = iter.next();
            if (current.getLast() == null) {
                iter.remove();
            }
        }
    }

}
