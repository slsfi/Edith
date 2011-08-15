package fi.finlit.edith.sql.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.CollectionTable;
import javax.persistence.ElementCollection;
import javax.persistence.Embedded;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Table;

@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @Embedded
    private NameForm normalized;

    @ElementCollection
    @CollectionTable(name = "place_nameform", joinColumns = @JoinColumn(name = "place_id"))
    private Set<NameForm> otherForms = new HashSet<NameForm>();

    public Place() {
    }

    public Place(NameForm normalized, Set<NameForm> otherForms) {
        this.normalized = normalized;
        this.otherForms = otherForms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NameForm getNormalized() {
        return normalized;
    }

    public Set<NameForm> getOtherForms() {
        return otherForms;
    }

    public void setNormalized(NameForm normalized) {
        this.normalized = normalized;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }

}
