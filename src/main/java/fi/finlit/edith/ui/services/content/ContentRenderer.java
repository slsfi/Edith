/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services.content;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;

import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Document;

/**
 * @author tiwe
 */
public interface ContentRenderer {

    void renderPageLinks(Document document, MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocument(Document document, MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer);

    void renderDocumentAsXML(Document document, List<DocumentNote> documentNotes, OutputStream out) throws IOException, XMLStreamException;

    void renderDocument(Document document, List<DocumentNote> documentNotes,
            MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocumentNotesAsXML(Document document, List<DocumentNote> documentNotes, MarkupWriter notesWriter);

}