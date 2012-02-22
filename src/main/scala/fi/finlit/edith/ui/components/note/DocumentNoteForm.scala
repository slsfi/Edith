package fi.finlit.edith.ui.components.note

import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.dto.SelectedText
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.pages.document.Annotate
import fi.finlit.edith.ui.services.DocumentDao
import fi.finlit.edith.ui.services.DocumentNoteDao
//remove if not needed
import scala.collection.JavaConversions._

class DocumentNoteForm {

  @Parameter
  @Property
  private var documentNoteOnEdit: DocumentNote = _

  @InjectPage
  private var page: Annotate = _

  private var updateLongTextSelection: SelectedText = _

  @Inject
  private var documentNoteDao: DocumentNoteDao = _

  @Inject
  private var documentDao: DocumentDao = _

  private var delete: Boolean = _

  def isSlsMode(): Boolean = page.isSlsMode

  def getUpdateLongTextSelection(): SelectedText = {
    if (updateLongTextSelection == null) {
      updateLongTextSelection = new SelectedText()
    }
    updateLongTextSelection
  }

  def setUpdateLongTextSelection(updateLongTextSelection: SelectedText) {
    this.updateLongTextSelection = updateLongTextSelection
  }

  def onPrepareFromDocumentNoteForm(docNoteId: Long) {
    if (documentNoteOnEdit == null) {
      documentNoteOnEdit = documentNoteDao.getById(docNoteId)
    }
  }

  def onSelectedFromDelete() {
    delete = true
  }

  def onSuccessFromDocumentNoteForm(): AnyRef = {
    try {
      var successMsg = "submit-success"
      var noteId = documentNoteOnEdit.getNote.getId
      if (delete) {
        documentDao.removeDocumentNotes(page.getDocument, documentNoteOnEdit)
        page.getNoteEdit.setNoteOnEdit(documentNoteOnEdit.getNote)
        page.getNoteEdit.setDocumentNoteOnEdit(null)
        successMsg = "delete-success"
      } else {
        if (updateLongTextSelection.isValid) {
          documentNoteOnEdit = documentDao.updateNote(documentNoteOnEdit, updateLongTextSelection)
        } else {
          documentNoteDao.save(documentNoteOnEdit)
        }
        page.getDocumentNotes.setSelectedNote(documentNoteOnEdit)
        page.getNoteEdit.setDocumentNoteOnEdit(documentNoteOnEdit)
      }
      page.getDocumentNotes.setNoteId(noteId)
      page.zoneWithInfo(successMsg).add("noteEditZone", page.getNoteEdit.getBlock).add("documentZone", page.getDocumentView).add("documentNotesZone", page.getDocumentNotes.getBlock)
    } catch {
      case e: Exception => page.zoneWithError("note-edition-failed", e)
    }
  }
}
