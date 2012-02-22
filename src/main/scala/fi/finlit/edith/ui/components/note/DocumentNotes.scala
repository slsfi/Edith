package fi.finlit.edith.ui.components.note

import java.util.Collections
import java.util.Comparator
import java.util.List
import org.apache.tapestry5.Block
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.DocumentNoteDao
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("classpath:js/jquery.scrollTo-min.js"))
class DocumentNotes {

  @InjectPage
  private var page: Annotate = _

  @Inject
  private var documentNotesBlock: Block = _

  private var documentNotes: List[DocumentNote] = _

  private var noteId: java.lang.Long = _

  @Inject
  private var documentNoteDao: DocumentNoteDao = _

  @Property
  private var documentNote: DocumentNote = _

  private var selectedNote: DocumentNote = _

  def getBlock(): Block = documentNotesBlock

  private val byPosition = new Comparator[DocumentNote]() {

    override def compare(n1: DocumentNote, n2: DocumentNote): Int = n1.getPosition - n2.getPosition
  }

  def getDocumentNotes(): List[DocumentNote] = {
    if (documentNotes == null) {
      documentNotes = documentNoteDao.getOfNote(noteId)
      Collections.sort(documentNotes, byPosition)
    }
    documentNotes
  }

  def getSelectedNote(): DocumentNote = {
    if (selectedNote == null) {
      getDocumentNotes
      selectedNote = if (documentNotes.size > 0) documentNotes.get(0) else null
    }
    selectedNote
  }

  def setSelectedNote(selectedNote: DocumentNote) {
    this.selectedNote = selectedNote
  }

  def getSelectedCssClass(): String = {
    if (documentNote.getId == getSelectedNote.getId) "selected-note" else ""
  }

  def onActionFromSelectDocumentNote(documentNoteId: Long): AnyRef = {
    selectedNote = documentNoteDao.getById(documentNoteId)
    page.getNoteEdit.setDocumentNoteOnEdit(selectedNote)
    page.getNoteEdit.getBlock
  }

  def setNoteId(noteId: java.lang.Long) {
    this.noteId = noteId
  }
}
