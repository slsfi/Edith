package fi.finlit.edith.domain;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

@ClassMapping(ns = EDITH.NS)
public class NameForm extends Identifiable {
    @Predicate
    private String description;

    @Predicate
    private String first;

    @Predicate
    private String last;

    public NameForm() {
    }

    public NameForm(String name, String description) {
        last = name;
        this.description = description;
    }

    public NameForm(String first, String last, String description) {
        this.first = first;
        this.last = last;
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        StringBuilder builder = new StringBuilder();
        if (first != null) {
            builder.append(first);
        }
        if (first != null && last != null) {
            builder.append(" ");
        }
        if (last != null) {
            builder.append(last);
        }
        return builder.toString();
    }

    public void setName(String name) {
        last = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getFirst() {
        return first;
    }

    public String getLast() {
        return last;
    }

    public void setLast(String last) {
        this.last = last;
    }

    public void setFirst(String first) {
        this.first = first;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (getId() == null ? 0 : getId().hashCode());
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
        NameForm other = (NameForm) obj;
        if (getId() == null) {
            if (other.getId() != null) {
                return false;
            }
        } else if (!getId().equals(other.getId())) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "NameForm [description=" + description + ", name=" + getName() + "]";
    }
}
