/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components.document;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.Document;
import com.mysema.edith.services.ContentRenderer;

public class Pages {

    @Inject
    private ContentRenderer renderer;

    @Parameter
    private Document document;

    @BeginRender
    public void beginRender(MarkupWriter writer) throws XMLStreamException, IOException {
        renderer.renderPageLinks(document, writer);
    }

    public void setDocument(Document document) {
        this.document = document;
    }

}
