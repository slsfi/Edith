package fi.finlit.edith.ui.mixins

import java.util.List
import org.apache.commons.lang.StringUtils
import org.apache.tapestry5.annotations.Import
import org.apache.tapestry5.json.JSONArray
import org.apache.tapestry5.json.JSONObject
import org.apache.tapestry5.services.javascript.JavaScriptSupport
import fi.finlit.edith.sql.domain.Place
//remove if not needed
import scala.collection.JavaConversions._

@Import(library = ("place-autocomplete.js"))
class PlaceAutocomplete extends JQueryAutocomplete {

  protected override def init(elementId: String, ajaxURI: String, renderSupport: JavaScriptSupport) {
    super.init(elementId, ajaxURI, renderSupport)
    renderSupport.addInitializerCall("placeAutocompleter", elementId)
  }

  protected override def generateResponse(matches: List[Any]): JSONArray = {
    var a = new JSONArray()
    for (o <- matches) {
      var place = o.asInstanceOf[Place]
      var json = new JSONObject()
      var builder = new StringBuilder(place.getNormalized.getName)
      if (place.getNormalized.getDescription != null) {
        builder.append(" - " + StringUtils.abbreviate(place.getNormalized.getDescription, 32))
      }
      json.put("value", builder.toString)
      json.put("id", place.getId)
      a.put(json)
    }
    a
  }
}
