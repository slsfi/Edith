package fi.finlit.edith.ui.components.note

import java.util.List
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.BeginRender
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.ioc.annotations.Inject
import fi.finlit.edith.sql.domain.DocumentNote
import fi.finlit.edith.ui.services.content.ContentRenderer
//remove if not needed
import scala.collection.JavaConversions._

class View {

  @Inject
  private var renderer: ContentRenderer = _

  @Parameter
  private var documentNotes: List[DocumentNote] = _

  @BeginRender
  def beginRender(writer: MarkupWriter) {
    renderer.renderDocumentNotes(documentNotes, writer)
  }
}
