package fi.finlit.edith.ui.services

import java.util.List
import org.apache.tapestry5.hibernate.annotations.CommitAfter
import fi.finlit.edith.sql.domain.Term
//remove if not needed
import scala.collection.JavaConversions._

trait TermDao extends Dao[Term, Long] {

  /**
   * Find matching terms by searching matches from basicForm -property.
   *
   * @param partial
   *            the start of the basicForm
   * @param maxResults
   *            the max results
   *
   */
  def findByStartOfBasicForm(partial: String, maxResults: Int): List[Term]

  @CommitAfter
  def remove(termId: java.lang.Long): Unit

  @CommitAfter
  def save(term: Term): Unit
}
