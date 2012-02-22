package fi.finlit.edith.ui.components

import org.apache.tapestry5.ComponentAction
import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.MarkupWriter
import org.apache.tapestry5.ValueEncoder
import org.apache.tapestry5.annotations.Environmental
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.Parameter
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.services.ComponentDefaultProvider
import org.apache.tapestry5.services.FormSupport
import org.apache.tapestry5.services.Request
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import InlineEditField._
import scala.reflect.BeanProperty
//remove if not needed
import scala.collection.JavaConversions._

object InlineEditField {

  class ProcessSubmission(controlName: String) extends ComponentAction[InlineEditField] {

    override def execute(component: InlineEditField) {
      component.processSubmission(controlName)
    }
  }
}

@Import(library = ("classpath:js/jquery-1.4.1.js", "InlineEditField.js"))
class InlineEditField {

  /**
   * The value to read (when rendering) or update (when the form is submitted).
   */
  @Parameter(required = true, autoconnect = true, principal = true)
  private var value: AnyRef = _

  /**
   * Value encoder for the value, usually determined automatically from the type of the property
   * bound to the value parameter.
   */
  @Parameter(required = true)
  private var encoder: ValueEncoder = _

  @BeanProperty
  var controlName: String = _

  @Environmental(false)
  private var formSupport: FormSupport = _

  @Environmental
  private var renderSupport: JavaScriptSupport = _

  @Inject
  private var resources: ComponentResources = _

  @Inject
  private var defaultProvider: ComponentDefaultProvider = _

  @Inject
  private var request: Request = _

  def defaultEncoder(): ValueEncoder = {
    defaultProvider.defaultValueEncoder("value", resources)
  }

  def beginRender(writer: MarkupWriter): Boolean = {
    if (formSupport == null) {
      throw new RuntimeException("The Hidden component must be enclosed by a Form component.")
    }
    controlName = formSupport.allocateControlName(resources.getId)
    formSupport.store(this, new ProcessSubmission(controlName))
    var encoded = encoder.toClient(value)
    writer.element("input", "type", "hidden", "name", controlName, "value", "")
    writer.end()
    var editId = "inlineField_" + controlName
    writer.element("div", "id", editId, "contentEditable", "true", "class", "editable")
    writer.write(encoded)
    writer.end()
    false
  }

  private def processSubmission(controlName: String) {
    var encoded = request.getParameter(controlName)
    var decoded = encoder.toValue(encoded)
    value = decoded
  }
}
