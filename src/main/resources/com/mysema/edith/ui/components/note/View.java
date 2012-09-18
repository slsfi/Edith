/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.components.note;

import java.util.List;

import org.hibernate.annotations.Parameter;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.ContentRenderer;

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
