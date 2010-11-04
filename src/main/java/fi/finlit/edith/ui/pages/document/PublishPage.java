/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.IncludeJavaScriptLibrary;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.ui.services.ContentRenderer;
import fi.finlit.edith.ui.services.DocumentNoteRepository;

@IncludeStylesheet("context:styles/tei.css")
@IncludeJavaScriptLibrary({ "classpath:jquery-1.4.1.js"})
@SuppressWarnings("unused")
public class PublishPage extends AbstractDocumentPage {
    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    @Inject
    @Symbol(EDITH.PUBLISH_PATH)
    private String PUBLISH_PATH;

    void setupRender() {
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocumentRevision());
    }

    @Inject
    private ContentRenderer renderer;

    void onActionFromPublish(String id) throws IOException, XMLStreamException {
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocumentRevision());
        MarkupWriter documentWriter = new MarkupWriterImpl();
        renderer.renderDocument(getDocumentRevision(), documentNotes, documentWriter);
        new File(PUBLISH_PATH).mkdirs();
        final String path = PUBLISH_PATH + "/" + getDocumentRevision().getDocument().getTitle();
        writeHtmlFile(path + "_document.html", documentWriter);

        MarkupWriter notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotes(documentNotes, notesWriter);
        writeHtmlFile(path + "_notes.html", notesWriter);
    }

    private void writeHtmlFile(String path, MarkupWriter writer) throws FileNotFoundException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(path);
            writer.toMarkup(pw);
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

}
