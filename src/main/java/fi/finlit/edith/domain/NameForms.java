package fi.finlit.edith.domain;

import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class NameForms {
    @Predicate
    private NameForm normalizedForm;

    @Predicate
    private Set<NameForm> otherForms;

    public NameForms() {
    }

    public NameForms(NameForm normalizedForm, Set<NameForm> otherForms) {
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (normalizedForm == null ? 0 : normalizedForm.hashCode());
        result = prime * result + (otherForms == null ? 0 : otherForms.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        NameForms other = (NameForms) obj;
        if (normalizedForm == null) {
            if (other.normalizedForm != null) {
                return false;
            }
        } else if (!normalizedForm.equals(other.normalizedForm)) {
            return false;
        }
        if (otherForms == null) {
            if (other.otherForms != null) {
                return false;
            }
        } else if (!otherForms.equals(other.otherForms)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NameForms [normalizedForm=" + normalizedForm + ", otherForms=" + otherForms + "]";
    }
}
