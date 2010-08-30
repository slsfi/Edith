package fi.finlit.edith.ui.components.note;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.xml.stream.XMLStreamException;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.NoteFormat;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteStatus;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.PersonRepository;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermLanguage;
import fi.finlit.edith.domain.TermRepository;
import fi.finlit.edith.ui.services.ParagraphParser;

public class NoteForm {

    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(NoteForm.class);

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

    @Property
    private NameForm loopPerson;

    @Property
    private NameForm loopPlace;

    @Inject
    private Messages messages;

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

    private Person person;

    @Inject
    private PersonRepository personRepository;

    @InjectComponent
    @Property
    private Zone personZone;

    @Property
    private boolean saveAsNew;

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

    public String getDescription() {
        if (noteOnEdit.getNote().getDescription() == null) {
            return null;
        }
        return noteOnEdit.getNote().getDescription().toString();
    }

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note.getTerm().createCopy() : new Term();
    }

    @Validate("required")
    public NoteFormat getFormat() {
        return noteOnEdit.getNote().getFormat();
    }

    public TermLanguage getLanguage() {
        return termOnEdit.getLanguage();
    }

    public String getNormalizedDescription() {
        if (isPerson()) {
            return getPerson().getNormalizedForm().getDescription();
        }
        return null;
    }

    public String getNormalizedFirst() {
        if (isPerson()) {
            return getPerson().getNormalizedForm().getFirst();
        }
        return null;
    }

    public String getNormalizedLast() {
        if (isPerson()) {
            return getPerson().getNormalizedForm().getLast();
        }
        return null;
    }

    private Person getPerson() {
        if (personId != null) {
            return personRepository.getById(personId);
        }
        return person;
    }

    public Set<NameForm> getPersons() {
        if (isPerson()) {
            return getPerson().getOtherForms();
        }
        return new HashSet<NameForm>();
    }

    public String getSearch() {
        return "";
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
        final NoteStatus[] availableStatuses = noteOnEdit.getStatus().equals(NoteStatus.INITIAL) ? new NoteStatus[] {
                NoteStatus.INITIAL, NoteStatus.DRAFT, NoteStatus.FINISHED }
                : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
        return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public int getTermInstances() {
        if (noteOnEdit.getNote().getTerm() != null) {
            return documentNoteRepository.getOfTerm(noteOnEdit.getNote().getTerm().getId()).size();
        }
        return 0;
    }

    public String getTimeOfBirth() {
        if (isPerson() && getPerson().getTimeOfBirth() != null) {
            return getPerson().getTimeOfBirth().asString();
        }
        return null;
    }

    public String getTimeOfDeath() {
        if (isPerson() && getPerson().getTimeOfDeath() != null) {
            return getPerson().getTimeOfDeath().asString();
        }
        return null;
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public boolean isPerson() {
        return person != null;
    }

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }

    Object onPerson(String id) {
        if (!isPerson()) {
            setPerson(personRepository.getById(id));
        }
        // TODO Remove multizoneupdate
        return personZone.getBody();
    }

    void onPrepareFromNoteEditForm(String noteRev) {
        noteOnEdit = documentNoteRepository.getById(noteRev); // .createCopy();
        if (!isPerson()) {
            setPerson(noteOnEdit.getNote().getPerson());
        }
        termOnEdit = getEditTerm(noteOnEdit.getNote());
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepository.findByStartOfBasicForm(partial, 10);
    }

    Collection<Person> onProvideCompletionsFromPerson(String partial) {
        return personRepository.findByStartOfFirstAndLastName(partial, 10);
    }

    Object onSuccessFromNoteEditForm() {
        DocumentNote documentNote;
        noteOnEdit.getNote().setPerson(getPerson());
        if (noteOnEdit.getStatus().equals(NoteStatus.INITIAL)) {
            noteOnEdit.setStatus(NoteStatus.DRAFT);
        }
        try {
            if (updateLongTextSelection.isValid()) {
                documentNote = documentRepository.updateNote(noteOnEdit, updateLongTextSelection);
            } else {
                if (saveAsNew) {
                    documentNote = documentNoteRepository.saveAsCopy(noteOnEdit);
                    saveAsNew = false;
                } else {
                    documentNote = documentNoteRepository.save(noteOnEdit);
                }
            }
        } catch (final Exception e) {
            logger.error(e.getMessage(), e);
            infoMessage = messages.format("note-addition-failed");
            return new MultiZoneUpdate(EDIT_ZONE, errorBlock);
        }

        // Handling the embedded term edit
        if (StringUtils.isNotBlank(termOnEdit.getBasicForm())) {
            saveTerm(documentNote);
        }

        // prepare view (with possibly new revision)
        if (documentNote.getSVNRevision() > documentRevision.getRevision()) {
            documentRevision.setRevision(documentNote.getSVNRevision());
        }
        selectedNotes = Collections.singletonList(documentNote);
        noteOnEdit = documentNote;
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        // noteId = noteOnEdit.getNote().getId();
        comments = noteOnEdit.getNote().getComments();
        submitSuccess = true;
        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    private void saveTerm(DocumentNote noteRevision) {
        // The idea is that language can be changed without a new term being created. It is a
        // bit hard to follow I admit. -vema
        final List<Term> terms = termRepository.findByBasicForm(termOnEdit.getBasicForm());
        Term term = terms.isEmpty() ? termOnEdit : null;
        for (final Term current : terms) {
            if (termOnEdit.getMeaning() == null && current.getMeaning() == null
                    || termOnEdit.getMeaning().equals(current.getMeaning())) {
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

    private void setPerson(Person person) {
        this.person = person;
        if (isPerson()) {
            personId = person.getId();
        }
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
            noteOnEdit.getNote().setSources(ParagraphParser.parseParagraph(sources));
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.setStatus(status);
    }

    public int getPersonInstances() {
        if (isPerson()) {
            return documentNoteRepository.getOfPerson(getPerson().getId()).size();
        }
        return 0;
    }

    Object onEditPerson(String id) {
        personId = id;
        return editPersonForm;
    }

    @Inject
    @Property
    private Block editPersonForm;

    @Property
    private String personId;

    @Parameter
    @Property
    private Block closeDialog;
}
