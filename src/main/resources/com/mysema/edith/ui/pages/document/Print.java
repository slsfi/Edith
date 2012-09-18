/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages.document;

import java.util.List;

import com.mysema.edith.EDITH;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.DocumentNoteDao;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(stylesheet = { "context:styles/base.css", "context:styles/edith.css",
        "context:styles/tei.css" })
@SuppressWarnings("unused")
public class Print extends AbstractDocumentPage {

    @Inject
    private DocumentNoteDao documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    @Inject
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean slsMode;

    void setupRender() {
        documentNotes = documentNoteRepository.getOfDocument(getDocument());
    }

    public String getShortForm() {
        if (slsMode) {
            return documentNote.getShortenedSelection();
        } else {
            return documentNote.getNote().getLemma();
        }
    }

    public String getDescription() {
        return documentNote.getNote().getDescription();
    }

}
