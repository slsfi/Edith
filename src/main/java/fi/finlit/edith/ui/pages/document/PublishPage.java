/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.IncludeStylesheet;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteRepository;
import fi.finlit.edith.ui.services.ContentRenderer;

@IncludeStylesheet("context:styles/tei.css")
@SuppressWarnings("unused")
public class PublishPage extends AbstractDocumentPage {

    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    void setupRender() {
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocumentRevision());
    }

    @Inject
    private ContentRenderer renderer;

    void onActionFromPublish(String id) throws IOException, XMLStreamException  {
        MarkupWriter documentWriter = new MarkupWriterImpl();
        renderer.renderDocument(getDocumentRevision(), documentWriter);
        writeHtmlFile("target/document.html", documentWriter);

        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocumentRevision());
        MarkupWriter notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotes(documentNotes, notesWriter);
        writeHtmlFile("target/notes.html", notesWriter);
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
