package fi.finlit.edith.sql.domain

import java.util.HashSet
import java.util.Set
import javax.persistence.CollectionTable
import javax.persistence.ElementCollection
import javax.persistence.Embedded
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.JoinColumn
import javax.persistence.Table
import fi.finlit.edith.Identifiable
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Entity
@Table(name = "place")
class Place() extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @Embedded
  @BeanProperty
  var normalized: NameForm = _

  @ElementCollection
  @CollectionTable(name = "place_nameform", joinColumns = @JoinColumn(name = "place_id"))
  @BeanProperty
  var otherForms = new HashSet[NameForm]()

  def this(normalized: NameForm, otherForms: Set[NameForm]) {
    this.normalized = normalized
    this.otherForms = otherForms
  }
}
