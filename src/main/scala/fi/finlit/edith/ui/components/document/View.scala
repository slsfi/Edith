package fi.finlit.edith.ui.components.document

import java.io.IOException
import java.util.List
import javax.xml.stream.XMLStreamException
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.BeginRender
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.services.content.ContentRenderer
//remove if not needed
import scala.collection.JavaConversions._

class View {

  @Inject
  private var renderer: ContentRenderer = _

  @Parameter
  private var document: Document = _

  @Parameter
  private var documentNotes: List[DocumentNote] = _

  @BeginRender
  def beginRender(writer: MarkupWriter) {
    if (documentNotes == null) {
      renderer.renderDocument(document, writer)
    } else {
      renderer.renderDocument(document, documentNotes, writer)
    }
  }
}
