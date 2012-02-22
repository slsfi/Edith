package fi.finlit.edith.ui.pages

import java.io.IOException
import org.apache.tapestry5.EventContext
import org.apache.tapestry5.annotations.InjectPage
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.Response
//remove if not needed
import scala.collection.JavaConversions._

class Index {

  @Inject
  private var response: Response = _

  @InjectPage
  private var documentsPage: Documents = _

  def onActivate(eventContext: EventContext): AnyRef = {
    if (eventContext.getCount > 0) {
      response.sendError(HttpError.PAGE_NOT_FOUND, "Page not found!")
    }
    documentsPage
  }
}
