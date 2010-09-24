/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;

import fi.finlit.edith.domain.LinkElement;
import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.ParagraphElement;

public class ParagraphFormatter {
    @Parameter(required = true)
    private Paragraph value;

    // FIXME
    private static final String root = "http://www.google.com/";

    @BeginRender
    void render(MarkupWriter writer) {
        StringBuilder builder = new StringBuilder();
        for (ParagraphElement element : value.getElements()) {
            if (element instanceof LinkElement) {
                LinkElement linkElement = (LinkElement) element;
                String reference = StringEscapeUtils.escapeHtml(linkElement.getReference());
                String string = StringEscapeUtils.escapeHtml(linkElement.getString());
                String result = "<a"
                        + (reference == null ? "" : " href=\"" + root + reference + "\"") + ">"
                        + string + "</a>";
                builder.append(result);
            } else {
                builder.append(element.toString());
            }
        }
        writer.writeRaw(builder.toString());
    }
}
