package fi.finlit.edith.sql.domain

import javax.persistence.Entity
import javax.persistence.EnumType
import javax.persistence.Enumerated
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
@Table(name = "term")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class Term extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @BeanProperty
  var basicForm: String = _

  @BeanProperty
  var meaning: String = _

  @Enumerated(EnumType.STRING)
  @BeanProperty
  var language: TermLanguage = _

  @BeanProperty
  var otherLanguage: String = _
}
