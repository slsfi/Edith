package fi.finlit.edith.ui.pages.document

import java.io.IOException
import org.apache.tapestry5.EventContext
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import org.apache.tapestry5.services.Response
import com.mysema.tapestry.core.Context
import fi.finlit.edith.EDITH
import fi.finlit.edith.sql.domain.Document
import fi.finlit.edith.ui.pages.HttpError
import fi.finlit.edith.ui.services.DocumentDao
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

class AbstractDocumentPage {

  @Inject
  @BeanProperty
  var documentDao: DocumentDao = _

  @BeanProperty
  var document: Document = _

  private var context: Context = _

  @Inject
  private var response: Response = _

  @Inject
  @Symbol(EDITH.SVN_DOCUMENT_ROOT)
  private var documentRoot: String = _

  def onActivate(ctx: EventContext) {
    context = new Context(ctx)
    if (ctx.getCount == 0) {
      response.sendError(HttpError.PAGE_NOT_FOUND, "No document ID given!")
    }
    try {
      document = documentDao.getById(ctx.get(classOf[Long], 0))
    } catch {
      case e: RuntimeException => {
        response.sendError(HttpError.PAGE_NOT_FOUND, "Document not found!")
        return
      }
    }
  }

  def onPassivate(): Array[Any] = context.toArray()

  def getDocumentPath(): String = {
    var svnPath = document.getPath
    svnPath.substring(documentRoot.length() + 1, svnPath.length())
  }
}
