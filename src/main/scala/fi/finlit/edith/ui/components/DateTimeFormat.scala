package fi.finlit.edith.ui.components

import java.util.Locale
import org.apache.tapestry5.BindingConstants
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.annotations.BeginRender
import org.apache.tapestry5.annotations.Mixin
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.annotations.SetupRender
import org.apache.tapestry5.corelib.mixins.RenderInformals
import org.apache.tapestry5.ioc.annotations.Inject
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormatter
//remove if not needed
import scala.collection.JavaConversions._

class DateTimeFormat {

  @Parameter(required = true)
  private var date: DateTime = _

  @Parameter(defaultPrefix = BindingConstants.LITERAL, required = true)
  private var format: String = _

  @Inject
  private var locale: Locale = _

  @Mixin
  private var renderInformals: RenderInformals = _

  private var formattedDate: String = _

  @SetupRender
  def setupRender() {
    var fmt = org.joda.time.format.DateTimeFormat.forPattern(format).withLocale(locale)
    formattedDate = fmt.print(date)
  }

  @BeginRender
  def beginRender(writer: MarkupWriter) {
    writer.write(formattedDate)
  }
}
