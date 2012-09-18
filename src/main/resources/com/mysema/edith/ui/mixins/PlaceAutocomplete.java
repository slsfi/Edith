package com.mysema.edith.ui.mixins;

import java.util.List;

import com.mysema.edith.domain.Place;

@Import(library = { "place-autocomplete.js" })
public class PlaceAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, JavaScriptSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInitializerCall("placeAutocompleter", elementId);
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object o : matches) {
            Place place = (Place) o;
            JSONObject json = new JSONObject();
            StringBuilder builder = new StringBuilder(place.getNormalized().getName());
            if (place.getNormalized().getDescription() != null) {
                builder.append(" - "
                        + StringUtils.abbreviate(place.getNormalized().getDescription(), 32));
            }
            json.put("value", builder.toString());
            json.put("id", place.getId());
            a.put(json);
        }

        return a;
    }
}
