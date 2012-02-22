package fi.finlit.edith.ui.mixins

import java.util.Collections
import java.util.List
import org.apache.tapestry5.ComponentEventCallback
import org.apache.tapestry5.ComponentResources
import org.apache.tapestry5.ContentType
import org.apache.tapestry5.EventConstants
import org.apache.tapestry5.Field
import org.apache.tapestry5.annotations.Environmental
import org.apache.tapestry5.annotations.Events
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.annotations.InjectContainer
import org.apache.tapestry5.internal.util.Holder
import org.apache.tapestry5.ioc.annotations.Inject
import org.apache.tapestry5.ioc.services.TypeCoercer
import org.apache.tapestry5.json.JSONArray
import org.apache.tapestry5.json.JSONObject
import org.apache.tapestry5.services.Request
import org.apache.tapestry5.services.ResponseRenderer
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import org.apache.tapestry5.util.TextStreamResponse
import JQueryAutocomplete._
//remove if not needed
import scala.collection.JavaConversions._

object JQueryAutocomplete {

  val EVENT_NAME = "jqueryautocomplete"

  private val PARAM_NAME = "term"
}

@Import(library = ("jquery-autocomplete.js", "classpath:js/jquery-1.5.1.min.js", "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/ui/jquery.ui.widget.js", "classpath:js/ui/jquery.ui.position.js", "classpath:js/ui/jquery.ui.autocomplete.js"), stylesheet = ("context:styles/smoothness/jquery-ui-1.8.12.custom.css", "context:styles/smoothness/jquery.ui.autocomplete.css"))
@Events(EventConstants.PROVIDE_COMPLETIONS)
class JQueryAutocomplete {

  @InjectContainer
  private var field: Field = _

  @Inject
  private var resources: ComponentResources = _

  @Environmental
  private var renderSupport: JavaScriptSupport = _

  @Inject
  private var request: Request = _

  @Inject
  private var coercer: TypeCoercer = _

  @Inject
  private var responseRenderer: ResponseRenderer = _

  def afterRender() {
    init(field.getClientId, resources.createEventLink(EVENT_NAME).toAbsoluteURI(), renderSupport)
  }

  protected def init(elementId: String, ajaxURI: String, support: JavaScriptSupport) {
    support.addInitializerCall("jQueryAutocompleter", new JSONObject("elementId", elementId, "url", ajaxURI))
  }

  def onJQueryAutocomplete(): AnyRef = {
    var input = request.getParameter(PARAM_NAME)
    val matchesHolder = Holder.create()
    matchesHolder.put(Collections.emptyList[Any]())
    var callback = new ComponentEventCallback[Any]() {

      override def handleResult(result: AnyRef): Boolean = {
        @SuppressWarnings("unchecked") var matches = coercer.coerce(result, classOf[List[_]])
        matchesHolder.put(matches)
        return true
      }
    }
    resources.triggerEvent(EventConstants.PROVIDE_COMPLETIONS, Array[Any](input), callback)
    var contentType = responseRenderer.findContentType(this)
    new TextStreamResponse(contentType.toString, generateResponse(matchesHolder.get).toString)
  }

  protected def generateResponse(matches: List[Any]): JSONArray = {
    var a = new JSONArray()
    for (match <- matches) {
      a.put(match)
    }
    a
  }
}
