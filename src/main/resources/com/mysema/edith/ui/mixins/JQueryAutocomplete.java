package com.mysema.edith.ui.mixins;

import java.util.Collections;
import java.util.List;

@Import(library = { "jquery-autocomplete.js", "classpath:js/jquery-1.5.1.min.js",
        "classpath:js/jquery-ui-1.8.12.custom.min.js", "classpath:js/ui/jquery.ui.widget.js",
        "classpath:js/ui/jquery.ui.position.js", "classpath:js/ui/jquery.ui.autocomplete.js" }, stylesheet = {
        "context:styles/smoothness/jquery-ui-1.8.12.custom.css",
        "context:styles/smoothness/jquery.ui.autocomplete.css" })
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
        support.addInitializerCall("jQueryAutocompleter", new JSONObject("elementId", elementId,
                "url", ajaxURI));
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
