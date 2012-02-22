package fi.finlit.edith.ui.mixins

import java.util.List
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.json.JSONArray
import org.apache.tapestry5.json.JSONObject
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import fi.finlit.edith.sql.domain.Person
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("person-autocomplete.js"))
class PersonAutocomplete extends JQueryAutocomplete {

  protected override def init(elementId: String, ajaxURI: String, renderSupport: JavaScriptSupport) {
    super.init(elementId, ajaxURI, renderSupport)
    renderSupport.addInitializerCall("personAutocompleter", elementId)
  }

  protected override def generateResponse(matches: List[Any]): JSONArray = {
    var a = new JSONArray()
    for (o <- matches) {
      var person = o.asInstanceOf[Person]
      var json = new JSONObject()
      var builder = new StringBuilder(person.getNormalized.getName)
      if (person.getNormalized.getDescription != null) {
        builder.append(" - " + StringUtils.abbreviate(person.getNormalized.getDescription, 32))
      }
      json.put("value", builder.toString)
      json.put("id", person.getId)
      a.put(json)
    }
    a
  }
}
