package fi.finlit.edith.sql.domain;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;

@Entity
public class Person {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @OneToOne
    private NameForm normalizedForm;

    public Person(NameForm nameForm, Object object) {
        this.normalizedForm = nameForm;
    }

    public Person() {
        // TODO Auto-generated constructor stub
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public void setNormalizedForm(NameForm normalizedForm) {
        this.normalizedForm = normalizedForm;
    }

    public NameForm getNormalizedForm() {
        return normalizedForm;
    }
}
