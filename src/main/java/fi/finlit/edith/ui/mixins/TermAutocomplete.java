package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.json.JSONArray;
import org.json.JSONObject;

import fi.finlit.edith.domain.Term;

@IncludeJavaScriptLibrary( { "term-autocomplete.js" })
public class TermAutocomplete extends JQueryAutocomplete {

    @Override
    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        super.init(elementId, ajaxURI, renderSupport);
        //Hooking our own function
        renderSupport.addInit("termAutocompleter", new JSONArray(elementId));       
    }

    @Override
    protected JSONArray generateResponse(List<Object> matches) {
        
        JSONArray a = new JSONArray();
        
        for(Object o : matches)  {
            //These are actually terms
            //XXX Is this good way to do this, the connection is not so clear
            Term term = (Term) o;
            //Putting out the necessary fields
            a.put(new JSONObject(term, new String[] {"basicForm","meaning"}));
        }
        
        return a;
    }
  
}
