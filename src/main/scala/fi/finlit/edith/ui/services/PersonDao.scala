package fi.finlit.edith.ui.services

import java.util.Collection
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.sql.domain.Person
//remove if not needed
import scala.collection.JavaConversions._

trait PersonDao extends Dao[Person, Long] {

  def findByStartOfFirstAndLastName(partial: String, limit: Int): Collection[Person]

  @CommitAfter
  def remove(personId: java.lang.Long): Unit

  @CommitAfter
  def save(person: Person): Unit
}
