/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

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
        PrintWriter pw = new PrintWriter("target/document.html");
        MarkupWriter writer = new MarkupWriterImpl();
        renderer.renderDocument(getDocumentRevision(), writer);
        writer.toMarkup(pw);
        pw.close();

//        File htmlNotes = new File("target/notes.html");
//        pw = new PrintWriter(htmlNotes);
//        writer = new MarkupWriterImpl();
//        renderer.renderPageLinks(getDocumentRevision(), writer);
//        writer.toMarkup(pw);
//        pw.close();


    }

}
