/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components.document;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.ContentRenderer;

public class View {

    @Inject
    private ContentRenderer renderer;

    @Parameter
    private Document document;

    @Parameter
    private List<DocumentNote> documentNotes;

    @BeginRender
    void beginRender(MarkupWriter writer) throws IOException, XMLStreamException {
        if (documentNotes == null) {
            renderer.renderDocument(document, writer);
        } else {
            renderer.renderDocument(document, documentNotes, writer);
        }
    }

}
