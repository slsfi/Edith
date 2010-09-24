/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.util.List;

import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;

/**
 * ViewPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@IncludeStylesheet("context:styles/tei.css")
@SuppressWarnings("unused")
public class ViewPage extends AbstractDocumentPage {

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
