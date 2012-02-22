package fi.finlit.edith.ui.pages.document

import java.util.List
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.services.DocumentNoteDao
//remove if not needed
import scala.collection.JavaConversions._

@Import(stylesheet = ("context:styles/tei.css", "Annotate.css"))
class View extends AbstractDocumentPage {

  @Inject
  private var documentNoteRepository: DocumentNoteDao = _

  @Property
  private var documentNote: DocumentNote = _

  @Property
  private var documentNotes: List[DocumentNote] = _

  def setupRender() {
    documentNotes = documentNoteRepository.getOfDocument(getDocument)
  }
}
