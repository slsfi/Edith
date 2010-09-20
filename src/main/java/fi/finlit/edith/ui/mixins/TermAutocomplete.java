package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.Term;

// TODO : use common superclass for TermAutocomplete and JQueryAutocomplete
@IncludeJavaScriptLibrary({ "term-autocomplete.js" })
public class TermAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        // Hooking our own function
        renderSupport.addInit("termAutocompleter", new JSONArray().put(elementId));
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object o : matches) {
            Term term = (Term) o;
            /* "value" is the item used by the autocompletion to visualize the list element. */
            JSONObject json = new JSONObject().put("basicForm", term.getBasicForm());
            json.put("language", String.valueOf(term.getLanguage()));
            json.put("meaning", term.getMeaning());
            json.put("id", term.getId());
            if (term.getMeaning() != null) {
                json.put("value",
                        term.getBasicForm() + " - " + StringUtils.abbreviate(term.getMeaning(), 32));
            } else {
                json.put("value", term.getBasicForm());
            }
            a.put(json);
        }

        return a;
    }

}
