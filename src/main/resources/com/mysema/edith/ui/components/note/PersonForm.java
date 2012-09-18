package com.mysema.edith.ui.components.note;

import java.util.HashSet;
import java.util.Set;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Interval;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Person;
import com.mysema.edith.services.PersonDao;
import com.mysema.edith.ui.components.InfoMessage;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@SuppressWarnings("unused")
public class PersonForm {

    @InjectComponent
    private InfoMessage infoMessage;

    @Property
    private NameForm loopPerson;

    @Parameter(required = true, allowNull = false)
    @Property
    private Block closeDialog;

    @Inject
    private PersonDao personDao;

    @Property
    private String newFirst;

    @Property
    private String newLast;

    @Property
    private String newDescription;

    @Property
    @Parameter
    private Long personId;

    private Person person;

    @Parameter
    @Property
    private Zone personZone;

    public void beginRender() {
        if (personId == null) {
            person = new Person(new NameForm(), new HashSet<NameForm>());
        } else {
            person = personDao.getById(personId);
        }
    }

    public Person getPerson() {
        return person;
    }

    public Set<NameForm> getPersons() {
        return getPerson().getOtherForms();
    }

    public String getTimeOfBirth() {
        return getPerson().getTimeOfBirth() == null ? null : getPerson().getTimeOfBirth()
                .asString();
    }

    public String getTimeOfDeath() {
        return getPerson().getTimeOfDeath() == null ? null : getPerson().getTimeOfDeath()
                .asString();
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

    void onPrepareFromPersonForm(long id) {
        if (person == null) {
            person = personDao.getById(id);
        }
    }

    public Object onSuccessFromPersonForm() {
        if (newFirst != null || newLast != null) {
            getPerson().getOtherForms().add(new NameForm(newFirst, newLast, newDescription));
        }
        getPerson().setOtherForms(copyAndRemoveEmptyNameForms(getPerson().getOtherForms()));
        if (person.getNormalized().isValid()) {
            personDao.save(getPerson());
            personId = getPerson().getId();
            infoMessage.addInfoMsg("create-success");
            MultiZoneUpdate update = new MultiZoneUpdate("dialogZone", closeDialog).add(
                    "infoMessageZone", infoMessage.getBlock());
            if (personZone != null) {
                update = update.add("personZone", personZone.getBody());
            }

            return update;
        } else {
            return null;
        }
    }

    private Set<NameForm> copyAndRemoveEmptyNameForms(Set<NameForm> nameForms) {
        Set<NameForm> result = new HashSet<NameForm>();
        for (NameForm nameForm : nameForms) {
            if (nameForm.isValid()) {
                result.add(nameForm);
            }
        }
        return result;
    }
}
