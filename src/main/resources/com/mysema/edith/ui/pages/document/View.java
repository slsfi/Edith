/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages.document;

import java.util.List;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.DocumentNoteDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(stylesheet = { "context:styles/tei.css", "Annotate.css" })
@SuppressWarnings("unused")
public class View extends AbstractDocumentPage {

    @Inject
    private DocumentNoteDao documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    void setupRender() {
        documentNotes = documentNoteRepository.getOfDocument(getDocument());
    }

}
