package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.Person;

@IncludeJavaScriptLibrary({ "person-autocomplete.js" })
public class PersonAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInit("personAutocompleter", new JSONArray().put(elementId));
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object o : matches) {
            Person person = (Person) o;
            JSONObject json = new JSONObject().put("value", person.getNormalizedForm().getName());
            json.put("id", person.getId());
            a.put(json);
        }

        return a;
    }
}
