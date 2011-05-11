/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.ui.services.DocumentNoteRepository;

@Import(stylesheet="context:styles/tei.css")
@SuppressWarnings("unused")
public class View extends AbstractDocument {

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    void setupRender() {
        documentNotes = documentNoteRepository.getOfDocument(getDocumentRevision());
    }

}
