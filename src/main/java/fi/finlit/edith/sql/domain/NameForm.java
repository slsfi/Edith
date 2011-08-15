package fi.finlit.edith.sql.domain;

import javax.persistence.Embeddable;

@Embeddable
public class NameForm {
    private String description;

    private String first;

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
    public String toString() {
        return "NameForm [description=" + description + ", name=" + getName() + "]";
    }
}
