package fi.finlit.edith.ui.components.note;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.ui.services.PersonRepository;
import fi.finlit.edith.ui.services.PlaceRepository;

@SuppressWarnings("unused")
public class SKSNoteForm extends AbstractNoteForm {

    @Inject
    @Property
    private Block editPersonForm;

    @Inject
    @Property
    private Block editPlaceForm;

    @Property
    private NameForm loopPerson;

    @Property
    private NameForm loopPlace;

    @InjectComponent
    @Property
    private Zone personZone;

    @InjectComponent
    @Property
    private Zone placeZone;

    private Place place;

    @Property
    private String placeId;

    @Inject
    private PlaceRepository placeRepository;

    private Person person;

    @Property
    private String personId;

    @Inject
    private PersonRepository personRepository;

    void onPrepareFromNoteEditForm(String noteId, String docNoteId) {

        super.onPrepareFromNoteEditForm(noteId, docNoteId);

        if (!isPerson()) {
            setPerson(getNoteOnEdit().getNote().getPerson());
        }
        if (!isPlace()) {
            setPlace(getNoteOnEdit().getNote().getPlace());
        }

    }

    Object onSuccessFromNoteEditForm() {
        getNoteOnEdit().getNote().setPerson(getPerson());
        getNoteOnEdit().getNote().setPlace(getPlace());
        return super.onSuccessFromNoteEditForm();
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

    private Person getPerson() {
        if (personId != null) {
            return personRepository.getById(personId);
        }
        return person;
    }

    Collection<Person> onProvideCompletionsFromPerson(String partial) {
        return personRepository.findByStartOfFirstAndLastName(partial, 10);
    }

    Collection<Place> onProvideCompletionsFromPlace(String partial) {
        return placeRepository.findByStartOfName(partial, 10);
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

    public int getPersonInstances() {
        if (isPerson()) {
            return getDocumentNoteRepository().getOfPerson(getPerson().getId()).size();
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
            return getDocumentNoteRepository().getOfPlace(getPlace().getId()).size();
        }
        return 0;
    }

    public Set<NameForm> getPlaces() {
        if (isPlace()) {
            return getPlace().getOtherForms();
        }
        return new HashSet<NameForm>();
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

}
