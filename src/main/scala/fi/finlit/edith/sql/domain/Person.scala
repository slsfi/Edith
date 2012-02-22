package fi.finlit.edith.sql.domain

import java.util.HashSet
import java.util.Set
import javax.persistence._
import fi.finlit.edith.Identifiable
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Entity
@Table(name = "person")
class Person() extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @Embedded
  @BeanProperty
  var normalized: NameForm = _

  @ElementCollection
  @CollectionTable(name = "person_nameform", joinColumns = @JoinColumn(name = "person_id"))
  @BeanProperty
  var otherForms = new HashSet[NameForm]()

  @Embedded
  @AttributeOverrides((@AttributeOverride(name = "start", column = @Column(name = "time_of_birth_start")), @AttributeOverride(name = "end", column = @Column(name = "time_of_birth_end"))))
  @BeanProperty
  var timeOfBirth: Interval = _

  @Embedded
  @AttributeOverrides((@AttributeOverride(name = "start", column = @Column(name = "time_of_death_start")), @AttributeOverride(name = "end", column = @Column(name = "time_of_death_end"))))
  @BeanProperty
  var timeOfDeath: Interval = _

  def this(normalized: NameForm) {
    this.normalized = normalized
  }

  def this(normalized: NameForm, otherForms: Set[NameForm]) {
    this(normalized)
    this.otherForms = otherForms
  }
}
