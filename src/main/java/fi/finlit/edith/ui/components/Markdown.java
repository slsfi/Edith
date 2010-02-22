/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.components;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringEscapeUtils;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;

public class Markdown {
    @Parameter(required = true)
    private String value;

    @Parameter
    private boolean raw = false;

    private static final String ITALIC_REPLACEMENT = "<em>$2</em>";
    private static final String BOLD_REPLACEMENT = "<strong>$2</strong>";

    private static final Pattern ITALIC_PATTERN = Pattern.compile("(\\*|_)(?=\\S)(.+?)(?<=\\S)\\1");
    private static final Pattern BOLD_PATTERN = Pattern.compile("(\\*\\*|__)(?=\\S)(.+?[*_]*)(?<=\\S)\\1");

    @BeginRender
    void render(MarkupWriter writer) {
        if (value == null) {
            return;
        }
        String result = raw ? value : StringEscapeUtils.escapeHtml(value);
        result = BOLD_PATTERN.matcher(result).replaceAll(BOLD_REPLACEMENT);
        result = ITALIC_PATTERN.matcher(result).replaceAll(ITALIC_REPLACEMENT);
        writer.writeRaw(result);
    }
}
