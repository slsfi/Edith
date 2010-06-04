package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.NameForm;

@IncludeJavaScriptLibrary( { "nameform-autocomplete.js" })
public class NameFormAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInit("nameFormAutocompleter", new JSONArray().put(elementId));
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

//        for (Object o : matches) {
            NameForm nameForm = new NameForm("Aleksis Kivi", "Aleksin norminimi.");
            /* "value" is the item used by the autocompletion to visualize the list element. */
            a.put(new JSONObject().put("normalizedName", nameForm.getName()).put(
                    "normalizedDescription", nameForm.getDescription()).put(
                    "value",
                    nameForm.getName() + " - "
                            + StringUtils.abbreviate(nameForm.getDescription(), 32)));
//        }

        return a;
    }

}
