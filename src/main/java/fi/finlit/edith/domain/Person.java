package fi.finlit.edith.domain;

import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Person extends Identifiable {
    
    @Predicate
    private NameForm normalizedForm;

    @Predicate
    private Set<NameForm> otherForms;

    @Predicate
    private Interval timeOfBirth;

    @Predicate
    private Interval timeOfDeath;

    public Person() {
    }

    public Person(NameForm normalizedForm, Set<NameForm> otherForms) {
        this.normalizedForm = normalizedForm;
        this.otherForms = otherForms;
    }

    public NameForm getNormalizedForm() {
        return normalizedForm;
    }

    public void setNormalizedForm(NameForm normalizedForm) {
        this.normalizedForm = normalizedForm;
    }

    public Set<NameForm> getOtherForms() {
        return otherForms;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }

    public void setTimeOfDeath(Interval timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

    public void setTimeOfBirth(Interval timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public Interval getTimeOfDeath() {
        return timeOfDeath;
    }

    public Interval getTimeOfBirth() {
        return timeOfBirth;
    }
}
