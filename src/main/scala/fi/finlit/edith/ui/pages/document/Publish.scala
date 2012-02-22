package fi.finlit.edith.ui.pages.document

import java.io.File
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.PrintWriter
import java.util.List
import javax.xml.stream.XMLStreamException
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.internal.services.MarkupWriterImpl
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.services.DocumentNoteDao
import fi.finlit.edith.ui.services.content.ContentRenderer
import fi.finlit.edith.ui.services.svn.SubversionService
//remove if not needed
import scala.collection.JavaConversions._

@Import(stylesheet = ("context:styles/tei.css", "Annotate.css"), library = ("classpath:js/jquery-1.4.1.js"))
class Publish extends AbstractDocumentPage {

  @Inject
  private var documentNoteRepository: DocumentNoteDao = _

  @Property
  private var documentNote: DocumentNote = _

  @Property
  private var documentNotes: List[DocumentNote] = _

  @Inject
  @Symbol(EDITH.PUBLISH_PATH)
  private var publishPath: String = _

  @Inject
  private var renderer: ContentRenderer = _

  @Inject
  private var subversionService: SubversionService = _

  def setupRender() {
    documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocument)
  }

  def onActionFromPublish(id: String) {
    var document = getDocument
    documentNotes = documentNoteRepository.getPublishableNotesOfDocument(document)
    new File(publishPath).mkdirs()
    val path = publishPath + "/" + document.getTitle
    var documentWriter = new MarkupWriterImpl()
    renderer.renderDocument(document, documentNotes, documentWriter)
    writeHtmlFile(path + "_document.html", documentWriter)
    var notesWriter = new MarkupWriterImpl()
    renderer.renderDocumentNotes(documentNotes, notesWriter)
    writeHtmlFile(path + "_notes.html", notesWriter)
    var file = new File(path)
    file.createNewFile()
    var out = new FileOutputStream(file)
    renderer.renderDocumentAsXML(document, documentNotes, out)
    notesWriter = new MarkupWriterImpl()
    renderer.renderDocumentNotesAsXML(document, documentNotes, notesWriter)
    writeHtmlFile(path + "_notes.xml", notesWriter)
  }

  private def writeHtmlFile(path: String, writer: MarkupWriter) {
    var pw: PrintWriter = null
    try {
      pw = new PrintWriter(path)
      writer.toMarkup(pw)
    } finally {
      if (pw != null) {
        pw.close()
      }
    }
  }
}
