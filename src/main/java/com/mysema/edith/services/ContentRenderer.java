/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;

/**
 * @author tiwe
 */
public interface ContentRenderer {

    /**
     * @param document
     * @param writer
     * @throws IOException
     * @throws XMLStreamException
     */
    void renderPageLinks(Document document, XMLStreamWriter writer) throws IOException,
            XMLStreamException;

    /**
     * @param document
     * @param writer
     * @throws IOException
     * @throws XMLStreamException
     */
    void renderDocument(Document document, XMLStreamWriter writer) throws IOException,
            XMLStreamException;

    /**
     * @param documentNotes
     * @param writer
     * @throws XMLStreamException
     */
    void renderDocumentNotes(List<DocumentNote> documentNotes, XMLStreamWriter writer) throws XMLStreamException;

    /**
     * @param document
     * @param documentNotes
     * @param out
     * @throws IOException
     * @throws XMLStreamException
     */
    void renderDocumentAsXML(Document document, List<DocumentNote> documentNotes, OutputStream out)
            throws IOException, XMLStreamException;

    /**
     * @param document
     * @param documentNotes
     * @param writer
     * @throws IOException
     * @throws XMLStreamException
     */
    void renderDocument(Document document, List<DocumentNote> documentNotes, XMLStreamWriter writer)
            throws IOException, XMLStreamException;

    /**
     * @param document
     * @param documentNotes
     * @param notesWriter
     * @throws XMLStreamException
     */
    void renderDocumentNotesAsXML(Document document, List<DocumentNote> documentNotes,
            XMLStreamWriter notesWriter) throws XMLStreamException;

}