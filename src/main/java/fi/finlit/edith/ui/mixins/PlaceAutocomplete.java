package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.Place;

@IncludeJavaScriptLibrary({ "place-autocomplete.js" })
public class PlaceAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInit("placeAutocompleter", new JSONArray().put(elementId));
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object o : matches) {
            Place place = (Place) o;
            JSONObject json = new JSONObject();
            StringBuilder builder = new StringBuilder(place.getNormalizedForm().getName());
            if (place.getNormalizedForm().getDescription() != null) {
                builder.append(" - "
                        + StringUtils.abbreviate(place.getNormalizedForm().getDescription(), 32));
            }
            json.put("value", builder.toString());
            json.put("id", place.getId());
            a.put(json);
        }

        return a;
    }
}
