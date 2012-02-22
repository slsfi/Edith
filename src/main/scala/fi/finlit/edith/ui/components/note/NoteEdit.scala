package fi.finlit.edith.ui.components.note

import java.util.List
import org.apache.tapestry5.Block
import org.apache.tapestry5.annotations.InjectComponent
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.sql.domain.Note
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.NoteDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class NoteEdit {

  @Inject
  private var noteEditBlock: Block = _

  @Inject
  private var noteDao: NoteDao = _

  @Inject
  private var documentNoteDao: DocumentNoteDao = _

  @BeanProperty
  var documentNoteOnEdit: DocumentNote = _

  @BeanProperty
  var noteOnEdit: Note = _

  @Property
  private var loopNote: Note = _

  @Property
  private var selectedNotes: List[DocumentNote] = _

  @InjectComponent
  @BeanProperty
  var comments: Comments = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  @BeanProperty
  var slsMode: Boolean = _

  def getNoteId(): java.lang.Long = {
    if (documentNoteOnEdit != null) documentNoteOnEdit.getId else null
  }

  def getBlock(): Block = noteEditBlock

  def getLemmaInstances(): Int = {
    documentNoteDao.getDocumentNoteCount(documentNoteOnEdit.getNote)
  }

  def setDocumentNoteOnEdit(documentNoteOnEdit: DocumentNote) {
    this.documentNoteOnEdit = documentNoteOnEdit
    if (documentNoteOnEdit != null) {
      setNoteOnEdit(documentNoteOnEdit.getNote)
    }
  }
}
