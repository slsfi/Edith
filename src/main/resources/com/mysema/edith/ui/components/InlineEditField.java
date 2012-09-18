/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components;

import org.hibernate.annotations.Parameter;

@Import(library = { "classpath:js/jquery-1.4.1.js", "InlineEditField.js" })
@SuppressWarnings("unused")
public class InlineEditField {

    /**
     * The value to read (when rendering) or update (when the form is
     * submitted).
     */
    @Parameter(required = true, autoconnect = true, principal = true)
    private Object value;

    /**
     * Value encoder for the value, usually determined automatically from the
     * type of the property bound to the value parameter.
     */
    @Parameter(required = true)
    private ValueEncoder encoder;

    private String controlName;

    @Environmental(false)
    private FormSupport formSupport;

    @Environmental
    private JavaScriptSupport renderSupport;

    @Inject
    private ComponentResources resources;

    @Inject
    private ComponentDefaultProvider defaultProvider;

    @Inject
    private Request request;

    ValueEncoder defaultEncoder() {
        return defaultProvider.defaultValueEncoder("value", resources);
    }

    @SuppressWarnings("serial")
    static class ProcessSubmission implements ComponentAction<InlineEditField> {
        private final String controlName;

        public ProcessSubmission(String controlName) {
            this.controlName = controlName;
        }

        @Override
        public void execute(InlineEditField component) {
            component.processSubmission(controlName);
        }
    }

    @SuppressWarnings("unchecked")
    boolean beginRender(MarkupWriter writer) {
        if (formSupport == null) {
            throw new RuntimeException("The Hidden component must be enclosed by a Form component.");
        }

        controlName = formSupport.allocateControlName(resources.getId());

        formSupport.store(this, new ProcessSubmission(controlName));

        String encoded = encoder.toClient(value);

        writer.element("input", "type", "hidden", "name", controlName, "value", "");
        writer.end();

        String editId = "inlineField_" + controlName;

        writer.element("div", "id", editId, "contentEditable", "true", "class", "editable");
        writer.write(encoded);
        writer.end();

        return false;
    }

    private void processSubmission(String controlName) {
        String encoded = request.getParameter(controlName);

        Object decoded = encoder.toValue(encoded);

        value = decoded;
    }

    public String getControlName() {
        return controlName;
    }

}
