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

import fi.finlit.edith.domain.*;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.PersonRepository;
import fi.finlit.edith.ui.services.PlaceRepository;
import fi.finlit.edith.ui.services.TermRepository;
import fi.finlit.edith.ui.services.TimeService;

@SuppressWarnings("unused")
public class NoteForm {

    private static final String EDIT_ZONE = "editZone";

    private static final Logger logger = LoggerFactory.getLogger(NoteForm.class);

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

    @Inject
    @Property
    private Block editPersonForm;

    @Inject
    @Property
    private Block editPlaceForm;

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

    @Property
    private String personId;

    @Inject
    private PersonRepository personRepository;

    @InjectComponent
    @Property
    private Zone personZone;

    private Place place;

    @Property
    private String placeId;

    @Inject
    private PlaceRepository placeRepository;

    @InjectComponent
    @Property
    private Zone placeZone;

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

    public String getDescription() {
        return noteOnEdit.getNote().getDescription();
    }

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note.getTerm() : new Term();
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

    public String getNormalizedPlaceDescription() {
        if (isPlace()) {
            return getPlace().getNormalizedForm().getDescription();
        }
        return null;
    }

    public String getNormalizedPlaceName() {
        if (isPlace()) {
            return getPlace().getNormalizedForm().getName();
        }
        return null;
    }

    private Person getPerson() {
        if (personId != null) {
            return personRepository.getById(personId);
        }
        return person;
    }

    public int getPersonInstances() {
        if (isPerson()) {
            return documentNoteRepository.getOfPerson(getPerson().getId()).size();
        }
        return 0;
    }

    public Set<NameForm> getPersons() {
        if (isPerson()) {
            return getPerson().getOtherForms();
        }
        return new HashSet<NameForm>();
    }

    private Place getPlace() {
        if (placeId != null) {
            return placeRepository.getById(placeId);
        }
        return place;
    }

    public int getPlaceInstances() {
        if (isPlace()) {
            return documentNoteRepository.getOfPlace(getPlace().getId()).size();
        }
        return 0;
    }

    public Set<NameForm> getPlaces() {
        if (isPlace()) {
            return getPlace().getOtherForms();
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
        return noteOnEdit.getNote().getSources();
    }

    public NoteStatus getStatus() {
        return noteOnEdit.getNote().getStatus();
    }

    public EnumSelectModel getStatusModel() {
        final NoteStatus[] availableStatuses = noteOnEdit.getNote().getStatus()
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
        if (person == null && personId != null) {
            person = personRepository.getById(personId);
        }
        return person != null;
    }

    public boolean isPlace() {
        if (place == null && placeId != null) {
            place = placeRepository.getById(placeId);
        }
        return place != null;
    }

    public boolean isSelected() {
        return getSelectedTypes().contains(type);
    }

    Object onEditPerson(String id) {
        personId = id;
        return editPersonForm;
    }

    Object onEditPlace(String id) {
        placeId = id;
        return editPlaceForm;
    }

    Object onPerson(String id) {
        if (!isPerson()) {
            setPerson(personRepository.getById(id));
        }
        return personZone.getBody();
    }

    Object onPlace(String id) {
        if (!isPlace()) {
            setPlace(placeRepository.getById(id));
        }
        return placeZone.getBody();
    }

    void onPrepareFromNoteEditForm(String noteId, String docNoteId) {
        System.err.println("noteForm.onPrepareFromNoteEditForm");
        if (docNoteId != null) {
            noteOnEdit = documentNoteRepository.getById(docNoteId); // .createCopy();
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

        if (!isPerson()) {
            setPerson(noteOnEdit.getNote().getPerson());
        }
        if (!isPlace()) {
            setPlace(noteOnEdit.getNote().getPlace());
        }
        termOnEdit = getEditTerm(noteOnEdit.getNote());
        System.err.println("noteForm.onPrepareFromNoteEditForm --");
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termRepository.findByStartOfBasicForm(partial, 10);
    }

    Collection<Person> onProvideCompletionsFromPerson(String partial) {
        return personRepository.findByStartOfFirstAndLastName(partial, 10);
    }

    Collection<Place> onProvideCompletionsFromPlace(String partial) {
        return placeRepository.findByStartOfName(partial, 10);
    }

    Object onSuccessFromNoteEditForm() {
        DocumentNote documentNote;
        noteOnEdit.getNote().setPerson(getPerson());
        noteOnEdit.getNote().setPlace(getPlace());


        logger.info("onSuccessFromNoteEditForm begins with documentNote " + noteOnEdit + ", note "
                + noteOnEdit.getNote());

        // Handling the embedded term edit
        if (StringUtils.isNotBlank(termOnEdit.getBasicForm())) {
            setTerm(noteOnEdit);
        }

        if (noteOnEdit.getNote().getStatus().equals(NoteStatus.INITIAL)) {
            noteOnEdit.getNote().setStatus(NoteStatus.DRAFT);
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
        comments = noteOnEdit.getNote().getComments();
        submitSuccess = true;

        return new MultiZoneUpdate(EDIT_ZONE, noteEdit).add("listZone", notesList)
                .add("documentZone", documentView).add("commentZone", commentZone.getBody());
    }

    public String getSubtextSources() {
        return noteOnEdit.getNote().getSubtextSources();
    }

    public void setSubtextSources(String subtextSources) throws XMLStreamException {
        if (subtextSources != null) {
            noteOnEdit.getNote().setSubtextSources(subtextSources);
        }
    }

    public void setDescription(String description) throws XMLStreamException {
        if (description != null) {
            noteOnEdit.getNote().setDescription(description);
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

    private void setPlace(Place place) {
        this.place = place;
        if (isPlace()) {
            placeId = place.getId();
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
            noteOnEdit.getNote().setSources(sources);
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.getNote().setStatus(status);
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
