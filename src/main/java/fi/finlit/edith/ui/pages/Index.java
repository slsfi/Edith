/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages;

import java.io.IOException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;


/**
 * Index provides
 *
 * @author tiwe
 * @version $Id$
 */
public class Index {
    @Inject
    private Response response;

    void onActivate(EventContext eventContext) throws IOException {
        if (eventContext.getCount() > 0) {
            response.sendError(404, "Page not found!");
        }
    }

}
