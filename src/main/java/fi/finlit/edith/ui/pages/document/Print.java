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

import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.ui.services.DocumentNoteDao;

@Import(stylesheet={
    "context:styles/base.css",
    "context:styles/edith.css",
    "context:styles/tei.css"
})
@SuppressWarnings("unused")
public class Print extends AbstractDocumentPage{
    
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
