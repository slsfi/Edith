/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tapestry5.StreamResponse;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;
import org.apache.tapestry5.util.TextStreamResponse;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.ui.pages.HttpError;
import fi.finlit.edith.ui.services.DocumentDao;

public class RawDocument {

    @Inject
    private DocumentDao documentRepository;

    @Inject
    private Response response;

    public void onActivate() throws IOException {
        response.sendError(HttpError.PAGE_NOT_FOUND, "Document id is not given");
    }

    public StreamResponse onActivate(String id) throws IOException {
        //Give xsl if it's requested
        if (id.endsWith(".xsl")) {
            return new TextStreamResponse("text/xsl", "<xsl:stylesheet version='1.0' " +
                "xmlns:xsl='http://www.w3.org/1999/XSL/Transform'>" +
                "<xsl:template match='/'><xsl:copy-of select='.'/></xsl:template>" +
                "</xsl:stylesheet>");
        }

        final Document document = documentRepository.getById(Long.parseLong(id));

        if (document == null) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "Could not find document with id: " + id);
            return null;
        }

        return new StreamResponse() {
            @Override
            public void prepareResponse(Response response) {
            }

            @Override
            public InputStream getStream() throws IOException {
                return documentRepository.getDocumentStream(document);
            }

            @Override
            public String getContentType() {
                return "text/xml; charset=utf-8";
            }
        };
    }

}
