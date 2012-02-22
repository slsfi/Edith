package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QPerson.person
import java.util.Collection
import fi.finlit.edith.sql.domain.Person
import fi.finlit.edith.ui.services.PersonDao
//remove if not needed
import scala.collection.JavaConversions._

class PersonDaoImpl extends AbstractDao[Person] with PersonDao {

  override def findByStartOfFirstAndLastName(partial: String, limit: Int): Collection[Person] = {
    query.from(person).where(person.normalized.first.startsWithIgnoreCase(partial).or(person.normalized.last.startsWithIgnoreCase(partial))).limit(limit).list(person)
  }

  override def remove(personId: java.lang.Long) {
    var entity = getById(personId)
    if (entity != null) {
      getSession.delete(entity)
    }
  }

  override def save(person: Person) {
    getSession.save(person)
  }

  override def getById(id: java.lang.Long): Person = {
    query.from(person).where(person.id eq id).uniqueResult(person)
  }
}
