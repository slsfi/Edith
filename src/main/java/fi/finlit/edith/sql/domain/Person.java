package fi.finlit.edith.sql.domain;

import java.util.HashSet;
import java.util.Set;

import javax.persistence.AttributeOverride;
import javax.persistence.AttributeOverrides;
import javax.persistence.Column;
import javax.persistence.Embedded;
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
@Table(name = "person")
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @ManyToOne
    @Cascade(value = CascadeType.SAVE_UPDATE)
    private NameForm normalizedForm;

    @ManyToMany
    @Cascade(value = CascadeType.SAVE_UPDATE)
    private Set<NameForm> otherForms = new HashSet<NameForm>();

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "time_of_birth_start")),
            @AttributeOverride(name = "end", column = @Column(name = "time_of_birth_end")) })
    private Interval timeOfBirth;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "start", column = @Column(name = "time_of_death_start")),
            @AttributeOverride(name = "end", column = @Column(name = "time_of_death_end")) })
    private Interval timeOfDeath;

    public Person() {
    }

    public Person(NameForm normalizedForm) {
        this.normalizedForm = normalizedForm;
    }

    public Person(NameForm normalizedForm, Set<NameForm> otherForms) {
        this(normalizedForm);
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

    public Interval getTimeOfBirth() {
        return timeOfBirth;
    }

    public Interval getTimeOfDeath() {
        return timeOfDeath;
    }

    public void setNormalizedForm(NameForm normalizedForm) {
        this.normalizedForm = normalizedForm;
    }

    public void setOtherForms(Set<NameForm> otherForms) {
        this.otherForms = otherForms;
    }

    public void setTimeOfBirth(Interval timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public void setTimeOfDeath(Interval timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

}
