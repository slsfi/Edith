package fi.finlit.edith.sql.domain

import javax.persistence.Column
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
@Table(name = "user")
@Cache(usage = CacheConcurrencyStrategy.READ_WRITE)
class User() extends Identifiable {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  @BeanProperty
  var id: java.lang.Long = _

  @BeanProperty
  var firstName: String = _

  @BeanProperty
  var lastName: String = _

  @BeanProperty
  var email: String = _

  @BeanProperty
  var password: String = _

  @Enumerated(EnumType.STRING)
  @BeanProperty
  var profile: Profile = _

  @Column(unique = true)
  @BeanProperty
  var username: String = _

  @BeanProperty
  var active: Boolean = _

  def this(username: String) {
    this.username = username
  }
}
