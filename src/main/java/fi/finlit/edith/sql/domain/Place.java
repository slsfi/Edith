package fi.finlit.edith.sql.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

@Entity
@Table(name = "place")
public class Place {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Cascade(value = CascadeType.SAVE_UPDATE)
    private NameForm normalizedForm;

    @ManyToMany
    @Cascade(value = CascadeType.SAVE_UPDATE)
    private Set<NameForm> otherForms = new HashSet<NameForm>();

    public Place() {
    }

    public Place(NameForm normalizedForm, Set<NameForm> otherForms) {
        this.normalizedForm = normalizedForm;
        this.otherForms = otherForms;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public NameForm getNormalizedForm() {
        return normalizedForm;
    }

    public Set<NameForm> getOtherForms() {
        return otherForms;
    }

    public void setNormalizedForm(NameForm normalizedForm) {
        this.normalizedForm = normalizedForm;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }

}
