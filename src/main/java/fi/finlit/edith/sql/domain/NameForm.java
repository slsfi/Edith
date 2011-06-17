package fi.finlit.edith.sql.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

// FIXME: Embedded?
@Entity
public class NameForm {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

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

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return "NameForm [description=" + description + ", name=" + getName() + "]";
    }
}
