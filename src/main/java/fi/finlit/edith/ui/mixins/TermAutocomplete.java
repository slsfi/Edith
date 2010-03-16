package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.Term;


@IncludeJavaScriptLibrary( { "term-autocomplete.js" })
public class TermAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        //Hooking our own function
        renderSupport.addInit("termAutocompleter", new JSONArray().put(elementId));
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for(Object o : matches)  {
            Term term = (Term) o;
            /* "value" is the item used by the autocompletion to visualize the list element. */
            a.put(new JSONObject().put("meaning", term.getMeaning()).put("value", term.getBasicForm()));
        }

        return a;
    }

}
