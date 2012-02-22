package fi.finlit.edith.sql.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import org.hibernate.annotations.Type
import org.joda.time.DateTime
import fi.finlit.edith.Identifiable
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Entity
@Table(name = "notecomment")
class NoteComment() extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @ManyToOne
  @BeanProperty
  var note: Note = _

  @BeanProperty
  var message: String = _

  @BeanProperty
  var username: String = _

  @Type(type = "org.joda.time.contrib.hibernate.PersistentDateTime")
  @BeanProperty
  var createdAt: DateTime = _

  def this(note: Note, message: String, username: String) {
    this.note = note
    this.message = message
    this.username = username
  }
}
