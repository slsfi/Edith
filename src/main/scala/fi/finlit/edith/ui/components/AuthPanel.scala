package fi.finlit.edith.ui.components

import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.ioc.annotations.Inject
//remove if not needed
import scala.collection.JavaConversions._

class AuthPanel extends AuthAwarePanel {

  @Inject
  private var resources: ComponentResources = _

  def isOnPage(page: String): Boolean = {
    resources.getPageName.equalsIgnoreCase(page)
  }
}
