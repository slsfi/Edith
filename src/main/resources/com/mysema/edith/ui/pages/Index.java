/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages;

import java.io.IOException;

import javax.naming.event.EventContext;

public class Index {
    @Inject
    private Response response;

    @InjectPage
    private Documents documentsPage;

    Object onActivate(EventContext eventContext) throws IOException {
        if (eventContext.getCount() > 0) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "Page not found!");
        }
        return documentsPage;
    }

}
