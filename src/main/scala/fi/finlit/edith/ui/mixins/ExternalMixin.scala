package fi.finlit.edith.ui.mixins

import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.InjectContainer
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.corelib.components.PageLink
import org.apache.tapestry5.ioc.annotations.Inject
//remove if not needed
import scala.collection.JavaConversions._

class ExternalMixin {

  @InjectContainer
  private var pageLink: PageLink = _

  @Parameter(defaultPrefix = "inherit:")
  private var pageName: String = _

  @Inject
  private var resources: ComponentResources = _

  def beforeRender(writer: MarkupWriter) {
    var onPage = resources.getPageName.equalsIgnoreCase(pageName)
    if (onPage) {
      writer.writeRaw("disabled")
    }
  }
}
