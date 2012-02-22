package fi.finlit.edith.ui.components.note

import java.util.ArrayList
import java.util.Collection
import java.util.regex.Pattern
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.Block
import org.apache.tapestry5.ajax.MultiZoneUpdate
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.corelib.components.Grid
import org.apache.tapestry5.corelib.components.Zone
import org.apache.tapestry5.grid.GridDataSource
import org.apache.tapestry5.ioc.Messages
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.sql.domain.NoteType
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.NoteDao
import SearchResults._
//remove if not needed
import scala.collection.JavaConversions._

object SearchResults {

  private val STRIP_TAGS = Pattern.compile("\\<.*?>", Pattern.DOTALL)

  private val MAX_STRIPPED_LENGTH = 30
}

@Import(library = ("SearchResults.js"))
class SearchResults {

  @InjectPage
  private var page: Annotate = _

  @Inject
  @Property
  private var notesList: Block = _

  @InjectComponent
  private var listZone: Zone = _

  @Inject
  private var noteDao: NoteDao = _

  @InjectComponent
  @Property
  private var grid: Grid = _

  @Property
  private var notes: GridDataSource = _

  @Property
  private var note: Note = _

  @Property
  private var documentNote: DocumentNote = _

  @Inject
  private var messages: Messages = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  @Property
  private var slsMode: Boolean = _

  def getBlock(): Block = notesList

  def getSearchResults(): Boolean = {
    notes = noteDao.findNotes(page.getSearchInfo)
    notes != null && notes.getAvailableRows > 0
  }

  def onInplaceUpdate() {
    getSearchResults
  }

  def getPageSize(): Int = 20

  def onSelectNote(noteId: Long): AnyRef = {
    page.getDocumentNotes.setNoteId(noteId)
    var selected = page.getDocumentNotes.getSelectedNote
    if (selected != null) {
      page.getNoteEdit.setDocumentNoteOnEdit(selected)
    } else {
      page.getNoteEdit.setNoteOnEdit(noteDao.getById(noteId))
    }
    new MultiZoneUpdate("documentNotesZone", page.getDocumentNotes.getBlock).add("noteEditZone", page.getNoteEdit.getBlock)
  }

  def getTypesString(): String = {
    var translated = new ArrayList[String]()
    for (t <- note.getTypes) {
      translated.add(messages.get(t.toString))
    }
    StringUtils.join(translated, ", ")
  }

  def getStatusString(): String = messages.get(note.getStatus.toString)

  private def stripTagsAndConcat(str: String, maxSize: Int): String = {
    if (str == null) {
      return null
    }
    StringUtils.abbreviate(STRIP_TAGS.matcher(str).replaceAll(""), maxSize)
  }

  def getTermMeaning(): String = {
    stripTagsAndConcat(note.getTerm.getMeaning, MAX_STRIPPED_LENGTH)
  }

  def getDescription(): String = {
    stripTagsAndConcat(note.getDescription, MAX_STRIPPED_LENGTH)
  }

  def getEditorsForNote(): String = note.getEditors

  object DocumentNoteType {

    class DocumentNoteType {
    }

    val NORMAL = new DocumentNoteType()

    val SEMI_ORPHAN = new DocumentNoteType()

    val ORPHAN = new DocumentNoteType()

    val ELSEWHERE = new DocumentNoteType()
  }

  def getDocumentNoteType(): DocumentNoteType = {
    if (documentNote.getDocument == null) {
      DocumentNoteType.ORPHAN
    } else if (documentNote.getFullSelection == null) {
      DocumentNoteType.SEMI_ORPHAN
    } else if (documentNote.getDocument != page.getDocument) {
      DocumentNoteType.ELSEWHERE
    } else {
      DocumentNoteType.NORMAL
    }
  }
}
