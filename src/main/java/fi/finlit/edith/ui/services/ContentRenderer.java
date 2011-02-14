/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * @author tiwe
 */
public interface ContentRenderer {

    void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer);

    void renderDocumentAsXML(DocumentRevision document, List<DocumentNote> documentNotes, OutputStream out) throws IOException, XMLStreamException;

    void renderDocument(DocumentRevision document, List<DocumentNote> documentNotes,
            MarkupWriter writer) throws IOException, XMLStreamException;

    void renderDocumentNotesAsXML(DocumentRevision document, List<DocumentNote> documentNotes, MarkupWriter notesWriter);

}