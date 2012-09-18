/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package com.mysema.edith.ui.components;

import java.util.Locale;

import org.hibernate.annotations.Parameter;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormatter;

public class DateTimeFormat {

    @Parameter(required = true)
    private DateTime date;

    @Parameter(defaultPrefix = BindingConstants.LITERAL, required = true)
    private String format;

    @Inject
    private Locale locale;

    @SuppressWarnings("unused")
    @Mixin
    private RenderInformals renderInformals;

    private String formattedDate;

    @SetupRender
    void setupRender() {
        DateTimeFormatter fmt = org.joda.time.format.DateTimeFormat.forPattern(format).withLocale(
                locale);
        formattedDate = fmt.print(date);
    }

    @BeginRender
    void beginRender(MarkupWriter writer) {
        writer.write(formattedDate);
    }

}
