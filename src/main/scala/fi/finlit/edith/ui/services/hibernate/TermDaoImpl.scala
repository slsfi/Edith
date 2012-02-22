package fi.finlit.edith.ui.services.hibernate

import fi.finlit.edith.sql.domain.QTerm.term
import java.util.List
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.ui.services.TermDao
//remove if not needed
import scala.collection.JavaConversions._

class TermDaoImpl extends AbstractDao[Term] with TermDao {

  override def findByStartOfBasicForm(partial: String, maxResults: Int): List[Term] = {
    query.from(term).where(term.basicForm.startsWith(partial)).limit(maxResults).list(term)
  }

  override def remove(id: java.lang.Long) {
    var term = getById(id)
    getSession.delete(term)
  }

  override def getById(id: java.lang.Long): Term = {
    query.from(term).where(term.id eq id).uniqueResult(term)
  }

  override def save(term: Term) {
    getSession.save(term)
  }
}
