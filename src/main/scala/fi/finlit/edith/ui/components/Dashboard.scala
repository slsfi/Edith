package fi.finlit.edith.ui.components

import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.annotations.Symbol
import fi.finlit.edith.EDITH
//remove if not needed
import scala.collection.JavaConversions._

class Dashboard extends AuthAwarePanel {

  @Inject
  private var resources: ComponentResources = _

  @Inject
  @Symbol(EDITH.EXTENDED_TERM)
  @Property
  private var slsMode: Boolean = _

  def isOnPage(page: String): Boolean = {
    resources.getPageName.equalsIgnoreCase(page)
  }
}
