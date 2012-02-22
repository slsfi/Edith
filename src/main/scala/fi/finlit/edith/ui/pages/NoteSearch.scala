package fi.finlit.edith.ui.pages

import org.apache.commons.lang.StringUtils.isBlank
import java.util.Collection
import java.util.HashSet
import org.apache.tapestry5.Asset
import org.apache.tapestry5.EventContext
import org.apache.tapestry5.annotations.AfterRender
import org.apache.tapestry5.annotations.Environmental
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Path
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import com.mysema.tapestry.core.Context
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.SqlPrimaryKeyEncoder
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("classpath:js/jquery-1.4.1.js", "deleteDialog.js"))
class NoteSearch {

  @Property
  private var searchTerm: String = _

  @Property
  private var editMode: String = _

  private var context: Context = _

  @Property
  private var notes: GridDataSource = _

  @Property
  private var note: Note = _

  @Inject
  private var noteDao: NoteDao = _

  @Inject
  private var documentDao: DocumentDao = _

  @Property
  private var encoder: SqlPrimaryKeyEncoder[Note] = _

  @Inject
  @Path("NoteSearch.css")
  private var stylesheet: Asset = _

  @Environmental
  private var support: JavaScriptSupport = _

  private var selectedNotes: Collection[Note] = _

  private var orphanNoteIds: Collection[Long] = _

  private var removeSelected: Boolean = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  @Property
  private var slsMode: Boolean = _

  @AfterRender
  def addStylesheet() {
    support.importStylesheet(stylesheet)
  }

  def onActionFromCancel() {
    context = new Context(searchTerm)
  }

  def onActionFromToggleEdit() {
    context = new Context(searchTerm, "edit")
  }

  def onSelectedFromRemoveSelected() {
    removeSelected = true
  }

  def onActivate(ctx: EventContext) {
    orphanNoteIds = noteDao.getOrphanIds
    if (selectedNotes == null) {
      selectedNotes = new HashSet[Note]()
    }
    if (ctx.getCount >= 1) {
      searchTerm = ctx.get(classOf[String], 0)
    }
    if (ctx.getCount >= 2) {
      editMode = ctx.get(classOf[String], 1)
    }
    context = new Context(ctx)
  }

  def onPassivate(): AnyRef = {
    if (context == null) null else context.toArray()
  }

  def onPrepare() {
    encoder = new SqlPrimaryKeyEncoder[Note](noteDao)
  }

  def onSuccessFromEdit() {
    if (removeSelected) {
      noteDao.removeNotes(selectedNotes)
    } else {
      for (editedNote <- encoder.getAllValues if !isBlank(editedNote.getLemma)) {
        var currentNote = noteDao.getById(editedNote.getId)
        currentNote.setLemma(editedNote.getLemma)
        noteDao.save(editedNote)
      }
    }
    context = new Context(searchTerm)
  }

  def onSuccessFromSearch() {
    context = new Context(searchTerm)
  }

  def setupRender() {
    notes = noteDao.queryNotes(if (searchTerm == null) "*" else searchTerm)
  }

  def isNoteSelected(): Boolean = selectedNotes.contains(note)

  def setNoteSelected(selected: Boolean) {
    if (selected) {
      selectedNotes.add(note)
    } else {
      selectedNotes.remove(note)
    }
  }

  def isNotesNotEmpty(): Boolean = notes.getAvailableRows > 0

  def isOrphanNote(): Boolean = orphanNoteIds.contains(note.getId)
}
