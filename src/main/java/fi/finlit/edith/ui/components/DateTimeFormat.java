package fi.finlit.edith.ui.components;

import java.util.Locale;

import org.apache.tapestry5.BindingConstants;
import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Mixin;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.corelib.mixins.RenderInformals;
import org.apache.tapestry5.ioc.annotations.Inject;
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
