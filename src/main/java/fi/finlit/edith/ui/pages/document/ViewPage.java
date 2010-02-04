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

import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * ViewPage provides
 *
 * @author tiwe
 * @version $Id$
 */
// @IncludeJavaScriptLibrary( { "classpath:jquery-1.3.2.js", "classpath:TapestryExt.js",
// "ViewPage.js" })
@IncludeStylesheet("context:styles/tei.css")
@SuppressWarnings("unused")
public class ViewPage extends AbstractDocumentPage {

    @Inject
    private NoteRevisionRepository noteRevisionRepo;

    @Property
    private NoteRevision note;

    @Property
    private List<NoteRevision> docNotes;

    void setupRender() {
        docNotes = noteRevisionRepo.getOfDocument(getDocumentRevision());
    }

    public String getNoteMetadata() {
        StringBuilder sb = new StringBuilder();
        if (note.getDescription() != null) {
            sb.append(", ");
            sb.append(note.getDescription());
        }
        if (note.getSubtextSources() != null) {
            sb.append(", ");
            sb.append(note.getSubtextSources());
        }
        if (note.getRevisionOf().getTerm() != null
                && note.getRevisionOf().getTerm().getMeaning() != null) {
            sb.append(", ");
            sb.append(note.getRevisionOf().getTerm().getMeaning());
        }
        return sb.toString();
    }

}
