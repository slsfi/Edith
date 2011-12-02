/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.apache.tapestry5.services.Response;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.ui.pages.HttpError;
import fi.finlit.edith.ui.services.DocumentDao;

public class AbstractDocumentPage {

    @Inject
    private DocumentDao documentDao;

    private Document document;

    private Context context;

    @Inject
    private Response response;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    public void onActivate(EventContext ctx) throws IOException {
        context = new Context(ctx);
        if (ctx.getCount() == 0) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "No document ID given!");
        }
        try {
            document = documentDao.getById(ctx.get(Long.class, 0));
        } catch (RuntimeException e) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "Document not found!");
            return;
        }
    }

    Object[] onPassivate() {
        return context.toArray();
    }

    public Document getDocument() {
        return document;
    }

    public String getDocumentPath() {
        String svnPath = document.getPath();
        return svnPath.substring(documentRoot.length() + 1, svnPath.length());
    }

    protected DocumentDao getDocumentDao() {
        return documentDao;
    }

}
