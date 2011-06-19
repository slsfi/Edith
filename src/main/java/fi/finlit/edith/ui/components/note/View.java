/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.components.note;

import java.util.List;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.ui.services.content.ContentRenderer;

public class View {
    @Inject
    private ContentRenderer renderer;

    @Parameter
    private List<DocumentNote> documentNotes;

    @BeginRender
    void beginRender(MarkupWriter writer) {
        renderer.renderDocumentNotes(documentNotes, writer);
    }

}
