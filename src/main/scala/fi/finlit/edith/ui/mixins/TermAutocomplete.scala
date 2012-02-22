package fi.finlit.edith.ui.mixins

import java.util.List
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.json.JSONArray
import org.apache.tapestry5.json.JSONObject
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import fi.finlit.edith.sql.domain.Term
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("term-autocomplete.js"))
class TermAutocomplete extends JQueryAutocomplete {

  protected override def init(elementId: String, ajaxURI: String, renderSupport: JavaScriptSupport) {
    super.init(elementId, ajaxURI, renderSupport)
    renderSupport.addInitializerCall("termAutocompleter", elementId)
  }

  protected override def generateResponse(matches: List[Any]): JSONArray = {
    var a = new JSONArray()
    for (o <- matches) {
      var term = o.asInstanceOf[Term]
      var json = new JSONObject().put("basicForm", term.getBasicForm)
      json.put("language", String.valueOf(term.getLanguage))
      json.put("meaning", term.getMeaning)
      json.put("id", term.getId)
      if (term.getMeaning != null) {
        json.put("value", term.getBasicForm + " - " + StringUtils.abbreviate(term.getMeaning, 32))
      } else {
        json.put("value", term.getBasicForm)
      }
      a.put(json)
    }
    a
  }
}
