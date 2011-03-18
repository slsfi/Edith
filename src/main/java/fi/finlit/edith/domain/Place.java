package fi.finlit.edith.domain;

import java.util.HashSet;
import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class Place extends Identifiable {

    @Predicate
    private NameForm normalizedForm;

    @Predicate
    private Set<NameForm> otherForms = new HashSet<NameForm>();

    public Place() {
    }

    public Place(NameForm normalizedForm, Set<NameForm> otherForms) {
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
}
