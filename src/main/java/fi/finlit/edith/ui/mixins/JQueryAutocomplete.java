package fi.finlit.edith.ui.mixins;

import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.ComponentEventCallback;
import org.apache.tapestry5.ComponentResources;
import org.apache.tapestry5.ContentType;
import org.apache.tapestry5.EventConstants;
import org.apache.tapestry5.Field;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.RenderSupport;
import org.apache.tapestry5.annotations.Environmental;
import org.apache.tapestry5.annotations.Events;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.InjectContainer;
import org.apache.tapestry5.internal.util.Holder;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.services.TypeCoercer;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.services.Request;
import org.apache.tapestry5.services.ResponseRenderer;
import org.apache.tapestry5.util.TextStreamResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@IncludeJavaScriptLibrary( { "jquery-autocomplete.js", "classpath:jquery-1.4.1.js",
        "classpath:ui/jquery.ui.core.js", "classpath:ui/jquery.ui.widget.js",
        "classpath:ui/jquery.ui.position.js", "classpath:ui/jquery.ui.autocomplete.js" })
@IncludeStylesheet( {"context:styles/themes/base/jquery-ui.css",
    "jquery-autocomplete.css"})
@Events(EventConstants.PROVIDE_COMPLETIONS)
public class JQueryAutocomplete {
    static final String EVENT_NAME = "jqueryautocomplete";

    private static final String PARAM_NAME = "term";

    private static final Logger logger = LoggerFactory.getLogger(JQueryAutocomplete.class);

    @InjectContainer
    private Field field;

    @Inject
    private ComponentResources resources;

    @Environmental
    private RenderSupport renderSupport;

    @Inject
    private Request request;

    @Inject
    private TypeCoercer coercer;

    @Inject
    private ResponseRenderer responseRenderer;

    void afterRender(MarkupWriter writer) {
        init(field.getClientId(), resources.createEventLink(EVENT_NAME).toAbsoluteURI(), renderSupport);
    }

    protected void init(String elementId, String ajaxURI, RenderSupport renderSupport) {
        renderSupport.addInit("jQueryAutocompleter", new JSONArray(elementId, ajaxURI));
    }

    Object onJQueryAutocomplete() {

        String input = request.getParameter(PARAM_NAME);

        final Holder<List<Object>> matchesHolder = Holder.create();

        // Default it to an empty list.
        matchesHolder.put(Collections.<Object> emptyList());

        ComponentEventCallback<Object> callback = new ComponentEventCallback<Object>() {
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
