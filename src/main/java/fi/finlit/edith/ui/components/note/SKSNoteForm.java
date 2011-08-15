package fi.finlit.edith.ui.components.note;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.NameForm;
import fi.finlit.edith.sql.domain.Person;
import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.ui.services.PersonDao;
import fi.finlit.edith.ui.services.PlaceDao;

@SuppressWarnings("unused")
public class SKSNoteForm extends AbstractNoteForm {
//
//    @Inject
//    @Property
//    private Block editPersonForm;
//
//    @Inject
//    @Property
//    private Block editPlaceForm;
//
//    @Property
//    private NameForm loopPerson;
//
//    @Property
//    private NameForm loopPlace;
//
//    @InjectComponent
//    @Property
//    private Zone personZone;
//
//    //TODO This does not belong here, term have to be edited in it's on component
//    @Property
//    private Term termOnEdit;
//
//    @InjectComponent
//    @Property
//    private Zone placeZone;
//
//    private Place place;
//
//    @Property
//    private Long placeId;
//
//    @Inject
//    private PlaceDao placeDao;
//
//    private Person person;
//
//    @Property
//    private Long personId;
//
//    @Inject
//    private PersonDao personDao;
//
//    @Override
//    void onPrepareFromNoteEditForm(long noteId) {
//
//        super.onPrepareFromNoteEditForm(noteId);
//
//        if (!isPerson()) {
//            setPerson(getNoteOnEdit().getPerson());
//        }
//        if (!isPlace()) {
//            setPlace(getNoteOnEdit().getPlace());
//        }
//
//    }
//
//    @Override
//    Object onSuccessFromNoteEditForm() {
//        getNoteOnEdit().setPerson(getPerson());
//        getNoteOnEdit().setPlace(getPlace());
//        return super.onSuccessFromNoteEditForm();
//    }
//
//    public String getTimeOfBirth() {
//        if (isPerson() && getPerson().getTimeOfBirth() != null) {
//            return getPerson().getTimeOfBirth().asString();
//        }
//        return null;
//    }
//
//    public String getTimeOfDeath() {
//        if (isPerson() && getPerson().getTimeOfDeath() != null) {
//            return getPerson().getTimeOfDeath().asString();
//        }
//        return null;
//    }
//
//    public boolean isPerson() {
//        if (person == null && personId != null) {
//            person = personDao.getById(personId);
//        }
//        return person != null;
//    }
//
//    public boolean isPlace() {
//        if (place == null && placeId != null) {
//            place = placeDao.getById(placeId);
//        }
//        return place != null;
//    }
//
//    private void setPerson(Person person) {
//        this.person = person;
//        if (isPerson()) {
//            personId = person.getId();
//        }
//    }
//
//    private void setPlace(Place place) {
//        this.place = place;
//        if (isPlace()) {
//            placeId = place.getId();
//        }
//    }
//
//    private Person getPerson() {
//        if (personId != null) {
//            return personDao.getById(personId);
//        }
//        return person;
//    }
//
//    Collection<Person> onProvideCompletionsFromPerson(String partial) {
//        return personDao.findByStartOfFirstAndLastName(partial, 10);
//    }
//
//    Collection<Place> onProvideCompletionsFromPlace(String partial) {
//        return placeDao.findByStartOfName(partial, 10);
//    }
//
//    public String getNormalizedDescription() {
//        if (isPerson()) {
//            return getPerson().getNormalized().getDescription();
//        }
//        return null;
//    }
//
//    public String getNormalizedFirst() {
//        if (isPerson()) {
//            return getPerson().getNormalized().getFirst();
//        }
//        return null;
//    }
//
//    public String getNormalizedLast() {
//        if (isPerson()) {
//            return getPerson().getNormalized().getLast();
//        }
//        return null;
//    }
//
//    public String getNormalizedPlaceDescription() {
//        if (isPlace()) {
//            return getPlace().getNormalized().getDescription();
//        }
//        return null;
//    }
//
//    public String getNormalizedPlaceName() {
//        if (isPlace()) {
//            return getPlace().getNormalized().getName();
//        }
//        return null;
//    }
//
//    public int getPersonInstances() {
//        if (isPerson()) {
//            return getDocumentNoteDao().getOfPerson(getPerson().getId()).size();
//        }
//        return 0;
//    }
//
//    public Set<NameForm> getPersons() {
//        if (isPerson()) {
//            return getPerson().getOtherForms();
//        }
//        return new HashSet<NameForm>();
//    }
//
//    private Place getPlace() {
//        if (placeId != null) {
//            return placeDao.getById(placeId);
//        }
//        return place;
//    }
//
//    public int getPlaceInstances() {
//        if (isPlace()) {
//            return getDocumentNoteDao().getOfPlace(getPlace().getId()).size();
//        }
//        return 0;
//    }
//
//    public Set<NameForm> getPlaces() {
//        if (isPlace()) {
//            return getPlace().getOtherForms();
//        }
//        return new HashSet<NameForm>();
//    }
//
//    Object onEditPerson(Long id) {
//        personId = id;
//        return editPersonForm;
//    }
//
//    Object onEditPlace(Long id) {
//        placeId = id;
//        return editPlaceForm;
//    }
//
//    Object onPerson(long id) {
//        if (!isPerson()) {
//            setPerson(personDao.getById(id));
//        }
//        return personZone.getBody();
//    }
//
//    Object onPlace(long id) {
//        if (!isPlace()) {
//            setPlace(placeDao.getById(id));
//        }
//        return placeZone.getBody();
//    }

}
