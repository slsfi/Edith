package fi.finlit.edith.ui.components.note;

import java.util.HashSet;
import java.util.Set;

import org.apache.tapestry5.Block;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.Interval;
import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.PersonRepository;

public class PersonForm {
    @Property
    private Person person;

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

    void onPrepare() {
        person = new Person(new NameForm(), new HashSet<NameForm>());
    }

    public Set<NameForm> getPersons() {
        return person.getOtherForms();
    }

    public String getTimeOfBirth() {
        return person.getTimeOfBirth() == null ? null : person.getTimeOfBirth().asString();
    }

    public String getTimeOfDeath() {
        return person.getTimeOfDeath() == null ? null : person.getTimeOfDeath().asString();
    }

    public void setTimeOfBirth(String time) {
        if (time != null) {
            person.setTimeOfBirth(Interval.fromString(time));
        }
    }

    public void setTimeOfDeath(String time) {
        if (time != null) {
            person.setTimeOfDeath(Interval.fromString(time));
        }
    }

    public Object onSuccessFromPersonForm() {
        person.getOtherForms().add(new NameForm(newFirst, newLast, newDescription));
        personRepository.save(person);
        return closeDialog;
    }
}
