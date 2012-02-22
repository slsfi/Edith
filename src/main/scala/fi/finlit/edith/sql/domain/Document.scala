package fi.finlit.edith.sql.domain

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.Table
import org.hibernate.annotations.Cache
import org.hibernate.annotations.CacheConcurrencyStrategy
import fi.finlit.edith.Identifiable
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

@Entity
@Table(name = "document")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Document extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @Column(unique = true)
  @BeanProperty
  var path: String = _

  @BeanProperty
  var title: String = _
}
