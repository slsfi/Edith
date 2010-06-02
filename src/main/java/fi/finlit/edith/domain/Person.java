package fi.finlit.edith.domain;

import java.util.Set;

import org.joda.time.LocalDate;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Person {
    @Predicate
    private NameForm normalizedForm;

    @Predicate
    private Set<NameForm> otherForms;

    @Predicate
    private LocalDate timeOfBirth;

    @Predicate
    private LocalDate timeOfDeath;

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

    public LocalDate getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setTimeOfDeath(LocalDate timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

    public void setTimeOfBirth(LocalDate timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public LocalDate getTimeOfDeath() {
        return timeOfDeath;
    }
}
