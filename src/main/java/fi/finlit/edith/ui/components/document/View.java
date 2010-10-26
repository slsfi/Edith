/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.document;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.ContentRenderer;

/**
 * ViewPanel provides
 *
 * @author tiwe
 * @version $Id$
 */
public class View {

    @Inject
    private ContentRenderer renderer;

    @Parameter
    private DocumentRevision document;

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
