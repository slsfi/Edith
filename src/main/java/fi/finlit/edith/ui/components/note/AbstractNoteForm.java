package fi.finlit.edith.ui.components.note;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.TermRepository;
import fi.finlit.edith.ui.services.TimeService;

@SuppressWarnings("unused")
public abstract class AbstractNoteForm {

    private static final String EDIT_ZONE = "editZone";

    private final Logger logger = LoggerFactory.getLogger(getClass());
    
    @Parameter
    @Property
    private Block closeDialog;

    @Parameter
    private Set<NoteComment> comments;

    @Parameter
    private Zone commentZone;

    @Property
    @Parameter
    private SelectedText createTermSelection;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Parameter
    private List<DocumentNote> documentNotes;

    @Inject
    private TimeService timeService;

    @Inject
    private DocumentRepository documentRepository;

    @Property
    @Parameter
    private DocumentRevision documentRevision;

    @Parameter
    private Block documentView;

    @Parameter
    private Block errorBlock;

    @Parameter
    private String infoMessage;
    
    @Inject
    private Messages messages;

    @Parameter
    private Block noteEdit;

    @Parameter
    private DocumentNote noteOnEdit;

    @Inject
    private NoteRepository noteRepository;

    @Parameter
    @Property
    private Block notesList;

    @Property
    private boolean saveAsNew;

    @Property
    private boolean saveTermAsNew;

    @Parameter
    private List<DocumentNote> selectedNotes;

    @Property
    private boolean submitSuccess;

    @Property
    @Parameter
    private Term termOnEdit;

    @Inject
    private TermRepository termRepository;

    @Property
    private NoteType type;

    @Property
    @Parameter
    private SelectedText updateLongTextSelection;
        
    @Inject @Symbol(EDITH.EXTENDED_TERM)
    @Property
    private boolean extendedTerm;

    public String getDescription() {
        return noteOnEdit.getNote().getConcept(extendedTerm).getDescription();
    }

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note.getTerm() : new Term();
    }

    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getNote().getFormat();
    }
    
    public DocumentNote getNoteOnEdit() {
        return noteOnEdit;
    }
    
    public void setNoteOnEdit(DocumentNote noteOnEdit) {
        this.noteOnEdit = noteOnEdit;
    }
    
    protected DocumentNoteRepository getDocumentNoteRepository() {
        return documentNoteRepository;
    }

    public TermLanguage getLanguage() {
        return termOnEdit.getLanguage();
    }


    public String getSearch() {
        return "";
    }

    public Set<NoteType> getSelectedTypes() {
        if (noteOnEdit.getConcept(extendedTerm).getTypes() == null) {
            noteOnEdit.getConcept(extendedTerm).setTypes(new HashSet<NoteType>());
        }
        return noteOnEdit.getConcept(extendedTerm).getTypes();
    }

    public String getSources() {
        return noteOnEdit.getConcept(extendedTerm).getSources();
    }

    public NoteStatus getStatus() {
        return noteOnEdit.getConcept(extendedTerm).getStatus();
    }

    public EnumSelectModel getStatusModel() {
        final NoteStatus[] availableStatuses = noteOnEdit.getConcept(extendedTerm).getStatus()
                .equals(NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL,
                NoteStatus.DRAFT, NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT,
                NoteStatus.FINISHED };
        return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public int getTermInstances() {
        if (noteOnEdit.getNote().getTerm() != null) {
            return documentNoteRepository.getOfTerm(noteOnEdit.getNote().getTerm().getId()).size();
        }
        return 0;
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

  

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }


    void onPrepareFromNoteEditForm(String noteId, String docNoteId) {
        System.err.println("noteForm.onPrepareFromNoteEditForm " + noteId + " " + docNoteId);
        if (docNoteId != null) {
            if (noteOnEdit == null){
                noteOnEdit = documentNoteRepository.getById(docNoteId); // .createCopy();
            }
        } else {
            noteOnEdit = new DocumentNote();
            noteOnEdit.setLocalId(String.valueOf(timeService.currentTimeMillis()));
            noteOnEdit.setNote(noteRepository.getById(noteId));
            noteOnEdit.setDocRevision(documentRevision);
            noteOnEdit.setDocument(documentRevision.getDocument());
            if (noteOnEdit.getSVNRevision() == null) {
                noteOnEdit.setSVNRevision(documentRevision.getRevision());
            }
        }
        
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        System.err.println("noteForm.onPrepareFromNoteEditForm --");
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepository.findByStartOfBasicForm(partial, 10);
    }

   
    Object onSuccessFromNoteEditForm() {
        DocumentNote documentNote;

        logger.info("onSuccessFromNoteEditForm begins with documentNote " + noteOnEdit + ", note "
                + noteOnEdit.getNote());

        // Handling the embedded term edit
        if (StringUtils.isNotBlank(termOnEdit.getBasicForm())) {
            setTerm(noteOnEdit);
        }

        if (noteOnEdit.getConcept(extendedTerm).getStatus().equals(NoteStatus.INITIAL)) {
            noteOnEdit.getConcept(extendedTerm).setStatus(NoteStatus.DRAFT);
        }
        try {
            if (updateLongTextSelection.isValid()) {
                logger.info("update long text selection: " + noteOnEdit);
                documentNote = documentRepository.updateNote(noteOnEdit, updateLongTextSelection);
            } else {
                if (saveAsNew) {
                    logger.info("note saved as new: " + noteOnEdit);
                    documentNote = documentNoteRepository.saveAsCopy(noteOnEdit);
                    saveAsNew = false;
                } else {
                    logger.info("note saved: " + noteOnEdit);
                    documentNote = documentNoteRepository.save(noteOnEdit);
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // prepare view (with possibly new revision)
        if (documentNote.getSVNRevision() > documentRevision.getRevision()) {
            documentRevision.setRevision(documentNote.getSVNRevision());
        }

        selectedNotes = Collections.singletonList(documentNote);
        noteOnEdit = documentNote;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        comments = noteOnEdit.getConcept(extendedTerm).getComments();
        submitSuccess = true;

        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    public String getSubtextSources() {
        return noteOnEdit.getConcept(extendedTerm).getSubtextSources();
    }

    public void setSubtextSources(String subtextSources) throws XMLStreamException {
        if (subtextSources != null) {
            noteOnEdit.getConcept(extendedTerm).setSubtextSources(subtextSources);
        }
    }

    public void setDescription(String description) throws XMLStreamException {
        if (description != null) {
            noteOnEdit.getConcept(extendedTerm).setDescription(description);
        }
    }

    public void setFormat(NoteFormat format) {
        noteOnEdit.getNote().setFormat(format);
    }

    @Validate("required")
    public void setLanguage(TermLanguage language) {
        termOnEdit.setLanguage(language);
    }

    public void setSearch(String s) {
        // Do nothing
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
            noteOnEdit.getConcept(extendedTerm).setSources(sources);
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.getConcept(extendedTerm).setStatus(status);
    }

    private void setTerm(DocumentNote documentNote) {
        Term term = null;
        Term oldTerm = termOnEdit;
        if (saveTermAsNew) {
            saveTermAsNew = false;
            term = new Term();
            term.setBasicForm(oldTerm.getBasicForm());
            term.setMeaning(oldTerm.getMeaning());
            term.setLanguage(oldTerm.getLanguage());
        } else {
            term = oldTerm;
        }
        documentNote.getNote().setTerm(term);
    }
}
