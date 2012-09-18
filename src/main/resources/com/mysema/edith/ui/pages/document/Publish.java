/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.ui.pages.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.services.ContentRenderer;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.SubversionService;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(stylesheet = { "context:styles/tei.css", "Annotate.css" }, library = { "classpath:js/jquery-1.4.1.js" })
@SuppressWarnings("unused")
public class Publish extends AbstractDocumentPage {
    @Inject
    private DocumentNoteDao documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    @Inject
    @Symbol(EDITH.PUBLISH_PATH)
    private String publishPath;

    @Inject
    private ContentRenderer renderer;

    @Inject
    private SubversionService subversionService;

    public void setupRender() {
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocument());
    }

    public void onActionFromPublish(String id) throws IOException, XMLStreamException {
        Document document = getDocument();

        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(document);
        new File(publishPath).mkdirs();
        final String path = publishPath + "/" + document.getTitle();

        // document as HTML
        MarkupWriter documentWriter = new MarkupWriterImpl();
        renderer.renderDocument(document, documentNotes, documentWriter);
        writeHtmlFile(path + "_document.html", documentWriter);

        // notes as HTML
        MarkupWriter notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotes(documentNotes, notesWriter);
        writeHtmlFile(path + "_notes.html", notesWriter);

        // document as TEI
        File file = new File(path);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        renderer.renderDocumentAsXML(document, documentNotes, out);

        // notes as XML
        notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotesAsXML(document, documentNotes, notesWriter);
        writeHtmlFile(path + "_notes.xml", notesWriter);
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
