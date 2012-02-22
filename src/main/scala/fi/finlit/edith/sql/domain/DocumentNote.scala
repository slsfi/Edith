package fi.finlit.edith.sql.domain

import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.ManyToOne
import javax.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import com.mysema.query.annotations.QueryInit
import fi.finlit.edith.Identifiable
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Entity
@Table(name = "documentnote")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class DocumentNote extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @ManyToOne
  @BeanProperty
  var document: Document = _

  @BeanProperty
  var publishable: Boolean = _

  @BeanProperty
  var revision: java.lang.Long = _

  @BeanProperty
  var deleted: Boolean = _

  @BeanProperty
  var fullSelection: String = _

  @BeanProperty
  var position: Int = _

  @BeanProperty
  var createdOn: Long = _

  @BeanProperty
  var shortenedSelection: String = _

  @BeanProperty
  var lemmaPosition: String = _

  @ManyToOne
  @QueryInit("*")
  @BeanProperty
  var note: Note = _
}
