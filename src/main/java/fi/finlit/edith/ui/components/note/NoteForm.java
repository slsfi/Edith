package fi.finlit.edith.ui.components.note;

import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.pages.document.AnnotatePage;
import fi.finlit.edith.ui.services.ParagraphParser;

@SuppressWarnings("unused")
public class NoteForm {
    
    private static final String EDIT_ZONE = "editZone";
    
    private static final Logger logger = LoggerFactory.getLogger(NoteForm.class);
    
    @Parameter
    private List<DocumentNote> documentNotes;
    
    @Property
    private boolean submitSuccess;
        
    @Property
    @Parameter
    private SelectedText createTermSelection;
    
    @Parameter
    private List<DocumentNote> selectedNotes;
    
    @Inject
    private DocumentNoteRepository documentNoteRepository;
    
    @Inject
    private DocumentRepository documentRepository;
    
    @Property
    @Parameter
    private DocumentRevision documentRevision;
    
    @Parameter
    private Block errorBlock;
    
    @Parameter
    private String infoMessage;
    
    @Property
    private NameForm loopPerson;
    
    @Inject
    private Messages messages;
    
    @Parameter
    private Zone commentZone;
    
    @Parameter
    private Block documentView;
    
    @Property
    private String newPersonDescription;
    
    @Property
    private String newPersonFirst;
    
    
    @Property
    private String newPersonLast;
    
    @Property
    private String newPlaceDescription;
    
    @Property
    private String newPlaceName;
    
    @Parameter
    private Block noteEdit;
    
    @Property
    @Parameter
    private DocumentNote noteOnEdit;
    
    @Inject
    private NoteRepository noteRepository;
    
    @Parameter
    @Property
    private Block notesList;
    
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
    
    @Property
    private NameForm loopPlace;
    
    @Parameter
    private Set<NoteComment> comments;
    
    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getNote().getFormat();
    }
    
    public void setFormat(NoteFormat format) {
        noteOnEdit.getNote().setFormat(format);
    }
    
    public TermLanguage getLanguage() {
        return termOnEdit.getLanguage();
    }
    
    public String getTimeOfBirth() {
        return noteOnEdit.getNote().getPerson().getTimeOfBirth() == null ? null : noteOnEdit.getNote().getPerson()
                .getTimeOfBirth().asString();
    }

    public String getTimeOfDeath() {
        return noteOnEdit.getNote().getPerson().getTimeOfDeath() == null ? null : noteOnEdit.getNote().getPerson()
                .getTimeOfDeath().asString();
    }
    
    @Validate("required")
    public void setLanguage(TermLanguage language) {
        termOnEdit.setLanguage(language);
    }
    
    public String getDescription() {
        if (noteOnEdit.getNote().getDescription() == null) {
            return null;
        }
        return noteOnEdit.getNote().getDescription().toString();
    }
    
    public String getSources() {
        if (noteOnEdit.getNote().getSources() == null) {
            return null;
        }
        return noteOnEdit.getNote().getSources().toString();
    }
    
    public void setTimeOfBirth(String time) {
        if (time != null) {
            noteOnEdit.getNote().getPerson().setTimeOfBirth(Interval.fromString(time));
        }
    }

    void onPrepareFromNoteEditForm(String noteRev) {
        noteOnEdit = documentNoteRepository.getById(noteRev).createCopy();
        termOnEdit = getEditTerm(noteOnEdit.getNote());
    }
    
    public void setTimeOfDeath(String time) {
        if (time != null) {
            noteOnEdit.getNote().getPerson().setTimeOfDeath(Interval.fromString(time));
        }
    }
    
    public NameForm getNormalizedPlace() {
        if (noteOnEdit.getNote().getPlace() == null) {
            noteOnEdit.getNote().setPlace(new Place(new NameForm(), new HashSet<NameForm>()));
        }
        return noteOnEdit.getNote().getPlace().getNormalizedForm();
    }
    
    public Set<NameForm> getPlaces() {
        return noteOnEdit.getNote().getPlace().getOtherForms();
    }
    
    public NameForm getNormalizedPerson() {
        if (noteOnEdit.getNote().getPerson() == null) {
            noteOnEdit.getNote().setPerson(new Person(new NameForm(), new HashSet<NameForm>()));
        }
        return noteOnEdit.getNote().getPerson().getNormalizedForm();
    }
    
    public Set<NameForm> getPersons() {
        return noteOnEdit.getNote().getPerson().getOtherForms();
    }
    
    public Set<NoteType> getSelectedTypes() {
        if (noteOnEdit.getNote().getTypes() == null) {
            noteOnEdit.getNote().setTypes(new HashSet<NoteType>());
        }
        return noteOnEdit.getNote().getTypes();
    }
    
    public NoteStatus getStatus() {
        return noteOnEdit.getStatus();
    }
    
    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.setStatus(status);
    }

   
    public EnumSelectModel getStatusModel() {
        NoteStatus[] availableStatuses = noteOnEdit.getStatus().equals(
                NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL, NoteStatus.DRAFT,
            NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
                return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }
    
    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }
    
    public void setDescription(String description) throws XMLStreamException {
        if (description != null) {
            noteOnEdit.getNote().setDescription(ParagraphParser.parseParagraph(description));
        }
    }
    
    public void setSources(String sources) throws XMLStreamException {
        if (sources != null) {
            noteOnEdit.getNote().setSources(ParagraphParser.parseParagraph(sources));
        }
    }
       
    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note
                .getTerm().createCopy() : new Term();
    }

    
    Object onSuccessFromNoteEditForm() throws IOException {
        DocumentNote noteRevision;
        if (noteOnEdit.getStatus().equals(NoteStatus.INITIAL)) {
            noteOnEdit.setStatus(NoteStatus.DRAFT);
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
                noteRevision = documentRepository.updateNote(noteOnEdit, updateLongTextSelection);
            } else {
                noteRevision = documentNoteRepository.save(noteOnEdit);
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
        if (noteRevision.getSVNRevision() > documentRevision.getRevision()) {
            documentRevision.setRevision(noteRevision.getSVNRevision());
        }
        documentNotes = documentNoteRepository.getOfDocument(documentRevision);
        selectedNotes = Collections.singletonList(noteRevision);
        noteOnEdit = noteRevision;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
//        noteId = noteOnEdit.getNote().getId();
        comments = noteOnEdit.getNote().getComments();
        submitSuccess = true;
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit)
            .add("listZone", notesList)
            .add("documentZone", documentView)
            .add("commentZone", commentZone.getBody());
    }

    private void saveTerm(DocumentNote noteRevision) {
        // The idea is that language can be changed without a new term being created. It is a
        // bit hard to follow I admit. -vema
        List<Term> terms = termRepository.findByBasicForm(termOnEdit.getBasicForm());
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
        termRepository.save(term);
        noteRevision.getNote().setTerm(term);
        noteRepository.save(noteRevision.getNote());
    }
    
    public void setSelected(boolean selected) {
        if (selected) {
            getSelectedTypes().add(type);
        } else {
            getSelectedTypes().remove(type);
        }
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
