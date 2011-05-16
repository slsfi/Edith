/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.internal.services.MarkupWriterImpl;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.ContentRenderer;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.svn.SubversionService;

@Import(stylesheet = { "context:styles/tei.css" }, library = { "classpath:js/jquery-1.4.1.js" })
@SuppressWarnings("unused")
public class Publish extends AbstractDocumentPage {
    @Inject
    private DocumentNoteRepository documentNoteRepository;

    @Property
    private DocumentNote documentNote;

    @Property
    private List<DocumentNote> documentNotes;

    @Inject
    @Symbol(EDITH.PUBLISH_PATH)
    private String PUBLISH_PATH;

    @Inject
    private ContentRenderer renderer;
    
    @Inject
    private SubversionService subversionService;

    public void setupRender() {
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(getDocumentRevision());
    }
    
    public void onActionFromPublish(String id) throws IOException, XMLStreamException {
        DocumentRevision revision = getDocumentRevision();
        documentNotes = documentNoteRepository.getPublishableNotesOfDocument(revision);
        new File(PUBLISH_PATH).mkdirs();
        final String path = PUBLISH_PATH + "/" + revision.getDocument().getTitle();
        
        // document as HTML
        MarkupWriter documentWriter = new MarkupWriterImpl();
        renderer.renderDocument(revision, documentNotes, documentWriter);
        writeHtmlFile(path + "_document.html", documentWriter);

        // notes as HTML
        MarkupWriter notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotes(documentNotes, notesWriter);
        writeHtmlFile(path + "_notes.html", notesWriter);
        
        // document as TEI
        File file = new File(path);
        file.createNewFile();
        FileOutputStream out = new FileOutputStream(file);
        renderer.renderDocumentAsXML(revision, documentNotes, out);
        
        // notes as XML
        notesWriter = new MarkupWriterImpl();
        renderer.renderDocumentNotesAsXML(revision, documentNotes, notesWriter);
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
