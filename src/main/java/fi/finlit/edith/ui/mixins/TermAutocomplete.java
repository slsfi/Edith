package fi.finlit.edith.ui.mixins;

import java.util.List;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.corelib.mixins.Autocomplete;
import org.apache.tapestry5.json.JSONLiteral;
import org.apache.tapestry5.json.JSONObject;

import fi.finlit.edith.domain.Term;

@IncludeJavaScriptLibrary( { "classpath:jquery-1.4.1.js", "TermAutocomplete.js" })
public class TermAutocomplete extends Autocomplete {

    @Override
    protected void configure(JSONObject config) {
       //config.put("updateElement", new JSONLiteral("function(el){update_term_autocomplete(el);}"));
        config.put("updateElement", new JSONLiteral("update_term_autocomplete"));
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void generateResponseMarkup(MarkupWriter writer, List matches) {
        writer.element("ul");

        for (Object o : matches) {
            Term t = (Term) o;

            writer.element("li");

            writer.element("div", "class", "basicForm");
            writer.write(t.getBasicForm());
            writer.end();

            writer.element("div", "class", "meaning");
            writer.write(t.getMeaning());
            writer.end();

            writer.end(); // li
        }

        writer.end(); // ul
    }

}
