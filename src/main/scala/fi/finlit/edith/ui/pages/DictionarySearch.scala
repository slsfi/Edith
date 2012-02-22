package fi.finlit.edith.ui.pages

import java.util.ArrayList
import java.util.Collection
import java.util.HashMap
import java.util.Map
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.EventContext
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.corelib.components.Grid
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.ioc.annotations.Inject
import com.mysema.tapestry.core.Context
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.TermDao
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("classpath:js/jquery-1.4.1.js", "deleteDialog.js"))
class DictionarySearch {

  @Property
  private var searchTerm: String = _

  private var context: Context = _

  @InjectComponent
  private var termsGrid: Grid = _

  @Property
  private var terms: GridDataSource = _

  @Property
  private var note: Note = _

  @Inject
  private var noteDao: NoteDao = _

  @Inject
  private var termDao: TermDao = _

  @Inject
  private var documentNoteDao: DocumentNoteDao = _

  @Property
  private var term: Term = _

  private var documentNotes: Map[Note, Collection[DocumentNote]] = _

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
    terms = noteDao.queryDictionary(if (searchTerm == null) "*" else searchTerm)
  }

  def onPassivate(): AnyRef = {
    if (context == null) null else context.toArray()
  }

  def onActionFromDelete(termId: Long) {
    termDao.remove(termId)
  }

  private def initDocumentNotes() {
    documentNotes = new HashMap[Note, Collection[DocumentNote]]()
  }

  def getFullSelections(): String = {
    if (documentNotes == null) {
      initDocumentNotes()
    }
    if (documentNotes.containsKey(note)) {
      var longTexts = new ArrayList[String]()
      for (documentNote <- documentNotes.get(note)) {
        longTexts.add(documentNote.getFullSelection)
      }
      return StringUtils.join(longTexts, ", ")
    }
    ""
  }
}
