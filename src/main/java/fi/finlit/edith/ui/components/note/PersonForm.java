package fi.finlit.edith.ui.components.note;

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Zone;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.PersonRepository;

public class PersonForm {
    @Property
    private NameForm loopPerson;

    @Parameter
    @Property
    private Block closeDialog;

    @Inject
    private PersonRepository personRepository;

    @Property
    private String newFirst;

    @Property
    private String newLast;

    @Property
    private String newDescription;

    @Property
    @Parameter
    private String personId;

    private Person person;

    @Parameter
    @Property
    private Zone personZone;

    public void beginRender() {
        if (personId == null) {
            person = new Person(new NameForm(), new HashSet<NameForm>());
        } else {
            person = personRepository.getById(personId);
        }
    }

    public Person getPerson() {
        return person;
    }

    public Set<NameForm> getPersons() {
        return getPerson().getOtherForms();
    }

    public String getTimeOfBirth() {
        return getPerson().getTimeOfBirth() == null ? null : getPerson().getTimeOfBirth().asString();
    }

    public String getTimeOfDeath() {
        return getPerson().getTimeOfDeath() == null ? null : getPerson().getTimeOfDeath().asString();
    }

    public void setTimeOfBirth(String time) {
        if (time != null) {
            getPerson().setTimeOfBirth(Interval.fromString(time));
        }
    }

    public void setTimeOfDeath(String time) {
        if (time != null) {
            getPerson().setTimeOfDeath(Interval.fromString(time));
        }
    }

    void onPrepareFromPersonForm() {
        if (person == null) {
            person = new Person(new NameForm(), new HashSet<NameForm>());
        }
    }

    void onPrepareFromPersonForm(String id) {
        if (person == null) {
            person = personRepository.getById(id);
        }
    }

    public Object onSuccessFromPersonForm() {
        if (newFirst != null || newLast != null) {
            getPerson().getOtherForms().add(new NameForm(newFirst, newLast, newDescription));
        }
        getPerson().setOtherForms(copyAndRemoveEmptyNameForms(getPerson().getOtherForms()));
        personRepository.save(getPerson());
        personId = getPerson().getId();
        if (personZone != null) {
            return new MultiZoneUpdate("dialogZone", closeDialog).add("personZone", personZone.getBody());
        }
        return closeDialog;
    }

    private Set<NameForm> copyAndRemoveEmptyNameForms(Set<NameForm> nameForms) {
        Set<NameForm> result = new HashSet<NameForm>();
        for (NameForm nameForm : nameForms) {
            if (nameForm.getFirst() != null || nameForm.getLast() != null) {
                result.add(nameForm);
            }
        }
        return result;
    }
}
