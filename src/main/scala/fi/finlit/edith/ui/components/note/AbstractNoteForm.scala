package fi.finlit.edith.ui.components.note

import java.util.List
import org.apache.tapestry5.ajax.MultiZoneUpdate
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.beaneditor.Validate
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.util.EnumSelectModel
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import fi.finlit.edith.dto.SelectedText
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.NoteStatus
import fi.finlit.edith.sql.domain.NoteType
import fi.finlit.edith.sql.domain.Term
import fi.finlit.edith.sql.domain.TermLanguage
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteDao
import fi.finlit.edith.ui.services.TermDao
import AbstractNoteForm._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

abstract object AbstractNoteForm {

  private val EDIT_ZONE = "editZone"
}

@Import(library = ("NoteForm.js"))
abstract class AbstractNoteForm {

  private val logger = LoggerFactory.getLogger(getClass)

  @InjectPage
  private var page: Annotate = _

  @Property
  @Parameter
  private var createTermSelection: SelectedText = _

  @Inject
  @BeanProperty
  var documentNoteDao: DocumentNoteDao = _

  @Inject
  private var documentDao: DocumentDao = _

  @Inject
  private var messages: Messages = _

  @Parameter
  @BeanProperty
  var noteOnEdit: Note = _

  @Inject
  private var noteDao: NoteDao = _

  @Property
  private var saveAsNew: Boolean = _

  @Property
  private var saveTermAsNew: Boolean = _

  @Parameter
  private var selectedNotes: List[DocumentNote] = _

  @Inject
  private var termDao: TermDao = _

  @Property
  private var `type`: NoteType = _

  private var delete: Boolean = _

  def onSelectedFromDelete() {
    delete = true
  }

  def onSelectedFromSaveAsNew() {
    saveAsNew = true
  }

  def isSlsMode(): Boolean = page.isSlsMode

  private def getEditTerm(note: Note): Term = {
    if (note.getTerm != null) note.getTerm else new Term()
  }

  def getLanguage(): TermLanguage = getEditTerm(noteOnEdit).getLanguage

  def getStatusModel(): EnumSelectModel = {
    val availableStatuses = if (noteOnEdit.getStatus == NoteStatus.INITIAL) Array[NoteStatus](NoteStatus.INITIAL, NoteStatus.DRAFT, NoteStatus.FINISHED) else Array[NoteStatus](NoteStatus.DRAFT, NoteStatus.FINISHED)
    new EnumSelectModel(classOf[NoteStatus], messages, availableStatuses)
  }

  def getTermInstances(): Int = {
    if (noteOnEdit.getTerm != null) {
      return documentNoteDao.getOfTerm(noteOnEdit.getTerm.getId).size
    }
    0
  }

  def getTypes(): Array[NoteType] = NoteType.values()

  def isSelected(): Boolean = noteOnEdit.getTypes.contains(`type`)

  def onPrepareFromNoteEditForm(noteId: Long) {
    noteOnEdit = noteDao.getById(noteId)
  }

  def onProvideCompletionsFromBasicForm(partial: String): List[Term] = {
    termDao.findByStartOfBasicForm(partial, 10)
  }

  def onSuccessFromNoteEditForm(): AnyRef = {
    logger.info("onSuccessFromNoteEditForm begins with " + noteOnEdit)
    if (noteOnEdit.getTerm != null) {
      logger.info("html edit contents: " + noteOnEdit.getTerm.getMeaning)
    }
    try {
      if (noteOnEdit.getStatus == NoteStatus.INITIAL) {
        noteOnEdit.setStatus(NoteStatus.DRAFT)
      }
      if (delete) {
        logger.info("note removed: " + noteOnEdit)
        noteDao.remove(noteOnEdit)
        page.getNoteEdit.setNoteOnEdit(null)
        return page.zoneWithInfo("delete-success").add("listZone", page.getSearchResults).add("noteEditZone", page.getNoteEdit)
      }
      logger.info("note saved: " + noteOnEdit)
      var update = page.zoneWithInfo("submit-success")
      if (saveAsNew) {
        noteDao.saveAsNew(noteOnEdit)
        page.getNoteEdit.setNoteOnEdit(noteOnEdit)
        page.zoneWithInfo("submit-success").add("listZone", page.getSearchResults).add("noteEditZone", page.getNoteEdit.getBlock)
      } else {
        noteDao.save(noteOnEdit)
        update.add("listZone", page.getSearchResults)
      }
    } catch {
      case e: Exception => page.zoneWithError("note-addition-failed", e)
    }
  }

  def setLanguage(language: TermLanguage) {
    getEditTerm(noteOnEdit).setLanguage(language)
  }

  def setSelected(selected: Boolean) {
    if (selected) {
      noteOnEdit.getTypes.add(`type`)
    } else {
      noteOnEdit.getTypes.remove(`type`)
    }
  }

  @Validate("required")
  def setStatus(status: NoteStatus) {
    noteOnEdit.setStatus(status)
  }

  def isDeletableNote(): Boolean = {
    documentNoteDao.getDocumentNoteCount(noteOnEdit) == 0
  }

  def getSearch(): String = ""

  def setSearch(search: String) {
  }
}
