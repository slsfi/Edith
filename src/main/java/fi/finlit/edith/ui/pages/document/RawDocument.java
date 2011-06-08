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

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.DocumentRepository;

public class RawDocument {

    @Inject
    private DocumentRepository documentRepository;

    public StreamResponse onActivate(String id) {
        final Document document = documentRepository.getById(id);

        return new StreamResponse() {
            @Override
            public void prepareResponse(Response response) {
            }

            @Override
            public InputStream getStream() throws IOException {
                return documentRepository.getDocumentStream(new DocumentRevision(document, -1));
            }

            @Override
            public String getContentType() {
                return "text/plain; charset=utf-8";
            }
        };
    }

}
