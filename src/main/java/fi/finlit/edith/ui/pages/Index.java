/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.io.IOException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

public class Index {
    @Inject
    private Response response;

    @InjectPage
    private DocumentsPage documentsPage;

    Object onActivate(EventContext eventContext) throws IOException {
        if (eventContext.getCount() > 0) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "Page not found!");
        }
        return documentsPage;
    }

}
