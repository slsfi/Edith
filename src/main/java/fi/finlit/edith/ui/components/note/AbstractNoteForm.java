package fi.finlit.edith.ui.components.note;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.annotations.InjectPage;
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
import fi.finlit.edith.ui.components.InfoMessage;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.TermRepository;
import fi.finlit.edith.ui.services.TimeService;

@SuppressWarnings("unused")
public abstract class AbstractNoteForm {

    private static final String EDIT_ZONE = "editZone";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @InjectPage
    private Annotate page;

    @Parameter
    private List<NoteComment> comments;

    @Parameter
    private Zone commentZone;

    @Property
    @Parameter
    private SelectedText createTermSelection;

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Inject
    private TimeService timeService;

    @Inject
    private DocumentRepository documentRepository;

    @Inject
    private Messages messages;

    @Parameter
    private Note noteOnEdit;

    @Inject
    private NoteRepository noteRepository;

    @Property
    private boolean saveAsNew;

    @Property
    private boolean saveTermAsNew;

    @Parameter
    private List<DocumentNote> selectedNotes;

    @Inject
    private TermRepository termRepository;

    @Property
    private NoteType type;
    
    private boolean delete;

    void onSelectedFromDelete() {
        delete = true;
    }
    
    public boolean isSlsMode() {
        return page.isSlsMode();
    }

    public String getDescription() {
        return noteOnEdit.getConcept(isSlsMode()).getDescription();
    }

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note.getTerm() : new Term();
    }

    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getFormat();
    }

    public Note getNoteOnEdit() {
        return noteOnEdit;
    }

    public void setNoteOnEdit(Note noteOnEdit) {
        this.noteOnEdit = noteOnEdit;
    }

    protected DocumentNoteRepository getDocumentNoteRepository() {
        return documentNoteRepository;
    }

    public TermLanguage getLanguage() {
        return getEditTerm(noteOnEdit).getLanguage();
    }

    public String getSearch() {
        return "";
    }

    public Set<NoteType> getSelectedTypes() {
        if (getNoteOnEditConcept().getTypes() == null) {
            getNoteOnEditConcept().setTypes(new HashSet<NoteType>());
        }
        return noteOnEdit.getConcept(isSlsMode()).getTypes();
    }

    public String getSources() {
        return getNoteOnEditConcept().getSources();
    }

    public NoteStatus getStatus() {
        return getNoteOnEditConcept().getStatus();
    }

    public EnumSelectModel getStatusModel() {
        final NoteStatus[] availableStatuses = getNoteOnEditConcept().getStatus().equals(
                NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL, NoteStatus.DRAFT,
                NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
        return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public int getTermInstances() {
        if (noteOnEdit.getTerm() != null) {
            return documentNoteRepository.getOfTerm(noteOnEdit.getTerm().getId()).size();
        }
        return 0;
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }

    void onPrepareFromNoteEditForm(String noteId) {
        System.err.println("noteForm.onPrepareFromNoteEditForm " + noteId);
        noteOnEdit = noteRepository.getById(noteId);
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepository.findByStartOfBasicForm(partial, 10);
    }

    Object onSuccessFromNoteEditForm() {
        logger.info("onSuccessFromNoteEditForm begins with " + noteOnEdit);

        try {
            
            if (getNoteOnEditConcept().getStatus().equals(NoteStatus.INITIAL)) {
                getNoteOnEditConcept().setStatus(NoteStatus.DRAFT);
            }

            // if (saveAsNew) {
            // logger.info("note saved as new: " + noteOnEdit);
            // documentNote = documentNoteRepository.saveAsCopy(noteOnEdit);
            // saveAsNew = false;

            if (delete) {
                
                logger.info("note removed: " + noteOnEdit);
                noteRepository.removeNote(noteOnEdit);
                page.getNoteEdit().setNoteOnEdit(null);
                return page.zoneWithInfo("delete-success")
                    .add("listZone", page.getSearchResults())
                    .add("noteEditZone", page.getNoteEdit());
                
            } else {
                
                logger.info("note saved: " + noteOnEdit);
                noteRepository.save(noteOnEdit);
                comments = NoteComment.getSortedComments(getNoteOnEditConcept().getComments());
                return page.zoneWithInfo("submit-success")
                    .add("listZone", page.getSearchResults());
                    
            }

            
            // .add("commentZone", commentZone.getBody())

        } catch (final Exception e) {
            return page.zoneWithError("note-addition-failed", e);
        }

    }

    public String getSubtextSources() {
        return getNoteOnEditConcept().getSubtextSources();
    }

    public void setSubtextSources(String subtextSources) throws XMLStreamException {
        if (subtextSources != null) {
            getNoteOnEditConcept().setSubtextSources(subtextSources);
        }
    }

    public void setDescription(String description) throws XMLStreamException {
        if (description != null) {
            getNoteOnEditConcept().setDescription(description);
        }
    }

    public void setFormat(NoteFormat format) {
        noteOnEdit.setFormat(format);
    }

    @Validate("required")
    public void setLanguage(TermLanguage language) {
        getEditTerm(noteOnEdit).setLanguage(language);
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
            getNoteOnEditConcept().setSources(sources);
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        getNoteOnEditConcept().setStatus(status);
    }

//    private void setTerm(DocumentNote documentNote) {
//        Term term = null;
//        Term oldTerm = termOnEdit;
//        if (saveTermAsNew) {
//            saveTermAsNew = false;
//            term = new Term();
//            term.setBasicForm(oldTerm.getBasicForm());
//            term.setMeaning(oldTerm.getMeaning());
//            term.setLanguage(oldTerm.getLanguage());
//        } else {
//            term = oldTerm;
//        }
//        documentNote.getNote().setTerm(term);
//    }

    public Concept getNoteOnEditConcept() {
        return noteOnEdit.getConcept(isSlsMode());
    }

    public String getEditorsForNoteOnEdit() {
        return noteOnEdit.getEditors(isSlsMode());
    }
    
    public boolean isDeletableNote() {
        return documentNoteRepository.getDocumentNoteCount(noteOnEdit) == 0;
    }
}
