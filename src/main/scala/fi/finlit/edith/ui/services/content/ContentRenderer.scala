package fi.finlit.edith.ui.services.content

import java.io.IOException
import java.io.OutputStream
import java.util.List
import javax.xml.stream.XMLStreamException
import org.apache.tapestry5.MarkupWriter
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
//remove if not needed
import scala.collection.JavaConversions._

/**
 * @author tiwe
 */
trait ContentRenderer {

  def renderPageLinks(document: Document, writer: MarkupWriter): Unit

  def renderDocument(document: Document, writer: MarkupWriter): Unit

  def renderDocumentNotes(documentNotes: List[DocumentNote], writer: MarkupWriter): Unit

  def renderDocumentAsXML(document: Document, documentNotes: List[DocumentNote], out: OutputStream): Unit

  def renderDocument(document: Document, documentNotes: List[DocumentNote], writer: MarkupWriter): Unit

  def renderDocumentNotesAsXML(document: Document, documentNotes: List[DocumentNote], notesWriter: MarkupWriter): Unit
}
