package fi.finlit.edith.ui.components

import org.apache.tapestry5.BindingConstants
import org.apache.tapestry5.Block
import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.Property
import org.apache.tapestry5.ioc.annotations.Inject
//remove if not needed
import scala.collection.JavaConversions._

@Import(stylesheet = ("context:styles/base.css", "context:styles/layout-3col.css", "context:styles/edith.css", "context:styles/tapestry/forms.css", "context:styles/tapestry/grid.css"))
class Layout {

  @Parameter(required = true, defaultPrefix = BindingConstants.LITERAL)
  @Property
  private var title: Block = _

  @Property
  private var pageName: String = _

  @Parameter(defaultPrefix = BindingConstants.LITERAL)
  @Property
  private var leftPanel: Block = _

  @Parameter(defaultPrefix = BindingConstants.LITERAL)
  @Property
  private var rightPanel: Block = _

  @Inject
  private var resources: ComponentResources = _

  def isOnPage(page: String): Boolean = {
    resources.getPageName.equalsIgnoreCase(page)
  }
}
