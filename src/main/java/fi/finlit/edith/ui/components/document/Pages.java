/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.document;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.ui.services.content.ContentRenderer;

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
