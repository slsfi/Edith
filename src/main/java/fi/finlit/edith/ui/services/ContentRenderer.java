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

    /**
     * @param document
     * @param writer
     * @throws Exception
     */
    void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    /**
     * @param document
     * @param writer
     * @throws Exception
     */
    void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    /**
     * @param documentNotes
     * @param writer
     */
    void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer);

    /**
     * @param document
     * @param documentNotes
     * @param out
     * @throws IOException 
     * @throws XMLStreamException 
     */
    void renderDocumentAsXML(DocumentRevision document, List<DocumentNote> documentNotes, OutputStream out) throws IOException, XMLStreamException;
    
    /**
     * @param document
     * @param documentNotes
     * @param writer
     * @throws IOException
     * @throws XMLStreamException
     */
    void renderDocument(DocumentRevision document, List<DocumentNote> documentNotes,
            MarkupWriter writer) throws IOException, XMLStreamException;

    /**
     * @param documentNotes
     * @param notesWriter
     */
    void renderDocumentNotesAsXML(DocumentRevision document, List<DocumentNote> documentNotes, MarkupWriter notesWriter);

}