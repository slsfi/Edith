package fi.finlit.edith.ui.pages

import org.apache.tapestry5.EventContext
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.ioc.annotations.Inject
import com.mysema.tapestry.core.Context
import fi.finlit.edith.sql.domain.Person
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.PersonDao
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("classpath:js/jquery-1.4.1.js", "deleteDialog.js"))
class PersonSearch {

  @Property
  private var searchTerm: String = _

  private var context: Context = _

  @Property
  private var persons: GridDataSource = _

  @Property
  private var person: Person = _

  @Inject
  private var noteDao: NoteDao = _

  @Inject
  private var personDao: PersonDao = _

  def onActivate(ctx: EventContext) {
    if (ctx.getCount >= 1) {
      searchTerm = ctx.get(classOf[String], 0)
    }
    context = new Context(ctx)
  }

  def onSuccessFromSearch() {
    context = new Context(searchTerm)
  }

  def setupRender() {
    persons = noteDao.queryPersons(if (searchTerm == null) "*" else searchTerm)
  }

  def onPassivate(): AnyRef = {
    if (context == null) null else context.toArray()
  }

  def onActionFromDelete(personId: Long) {
    personDao.remove(personId)
  }

  def getTimeOfBirth(): String = {
    if (person.getTimeOfBirth != null) {
      return person.getTimeOfBirth.asString()
    }
    null
  }

  def getTimeOfDeath(): String = {
    if (person.getTimeOfDeath != null) {
      return person.getTimeOfDeath.asString()
    }
    null
  }
}
