package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;

import fi.finlit.edith.sql.domain.Person;

@Import(library = { "person-autocomplete.js" })
public class PersonAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, JavaScriptSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInitializerCall("personAutocompleter", elementId);
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object o : matches) {
            Person person = (Person) o;
            JSONObject json = new JSONObject();
            StringBuilder builder = new StringBuilder(person.getNormalized().getName());
            if (person.getNormalized().getDescription() != null) {
                builder.append(" - " + StringUtils.abbreviate(person.getNormalized().getDescription(), 32));
            }
            json.put("value", builder.toString());
            json.put("id", person.getId());
            a.put(json);
        }

        return a;
    }
}
