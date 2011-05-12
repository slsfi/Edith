package fi.finlit.edith.ui.mixins;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.internal.util.Holder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ResponseRenderer;
import org.apache.tapestry5.services.javascript.JavaScriptSupport;
import org.apache.tapestry5.util.TextStreamResponse;

@Import(library = { "jquery-autocomplete.js", "classpath:js/jquery-1.4.1.js",
        "classpath:js/ui/jquery.ui.core.js", "classpath:js/ui/jquery.ui.widget.js",
        "classpath:js/ui/jquery.ui.position.js", "classpath:js/ui/jquery.ui.autocomplete.js" }, stylesheet = {
        "context:styles/themes/base/jquery-ui.css", "jquery-autocomplete.css" })
@Events(EventConstants.PROVIDE_COMPLETIONS)
public class JQueryAutocomplete {
    static final String EVENT_NAME = "jqueryautocomplete";

    private static final String PARAM_NAME = "term";

    @InjectContainer
    private Field field;

    @Inject
    private ComponentResources resources;

    @Environmental
    private JavaScriptSupport renderSupport;

    @Inject
    private Request request;

    @Inject
    private TypeCoercer coercer;

    @Inject
    private ResponseRenderer responseRenderer;

    void afterRender() {
        init(field.getClientId(), resources.createEventLink(EVENT_NAME).toAbsoluteURI(),
                renderSupport);
    }

    protected void init(String elementId, String ajaxURI, JavaScriptSupport support) {
        support.addInitializerCall("jQueryAutocompleter", new JSONObject("elementId", elementId, "url", ajaxURI));
    }

    Object onJQueryAutocomplete() {

        String input = request.getParameter(PARAM_NAME);

        final Holder<List<Object>> matchesHolder = Holder.create();

        // Default it to an empty list.
        matchesHolder.put(Collections.<Object> emptyList());

        ComponentEventCallback<Object> callback = new ComponentEventCallback<Object>() {
            @Override
            public boolean handleResult(Object result) {
                @SuppressWarnings("unchecked")
                List<Object> matches = coercer.coerce(result, List.class);

                matchesHolder.put(matches);

                return true;
            }
        };

        resources
                .triggerEvent(EventConstants.PROVIDE_COMPLETIONS, new Object[] { input }, callback);

        ContentType contentType = responseRenderer.findContentType(this);

        return new TextStreamResponse(contentType.toString(), generateResponse(matchesHolder.get())
                .toString());
    }

    protected JSONArray generateResponse(List<Object> matches) {
        JSONArray a = new JSONArray();

        for (Object match : matches) {
            a.put(match);
        }
        return a;
    }

}
