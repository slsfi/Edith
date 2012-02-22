package fi.finlit.edith.ui.pages.document

import java.io.IOException
import java.io.InputStream
import org.apache.tapestry5.StreamResponse
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.Response
import org.apache.tapestry5.util.TextStreamResponse
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.ui.pages.HttpError
import fi.finlit.edith.ui.services.DocumentDao
//remove if not needed
import scala.collection.JavaConversions._

class RawDocument {

  @Inject
  private var documentRepository: DocumentDao = _

  @Inject
  private var response: Response = _

  def onActivate() {
    response.sendError(HttpError.PAGE_NOT_FOUND, "Document id is not given")
  }

  def onActivate(id: String): StreamResponse = {
    if (id.endsWith(".xsl")) {
      return new TextStreamResponse("text/xsl", "<xsl:stylesheet version='1.0' " + "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" + "<xsl:template match='/'><xsl:copy-of select='.'/></xsl:template>" + "</xsl:stylesheet>")
    }
    val document = documentRepository.getById(Long.parseLong(id))
    if (document == null) {
      response.sendError(HttpError.PAGE_NOT_FOUND, "Could not find document with id: " + id)
      return null
    }
    new StreamResponse() {

      override def prepareResponse(response: Response) {
      }

      override def getStream(): InputStream = {
        documentRepository.getDocumentStream(document)
      }

      override def getContentType(): String = "text/xml; charset=utf-8"
    }
  }
}
