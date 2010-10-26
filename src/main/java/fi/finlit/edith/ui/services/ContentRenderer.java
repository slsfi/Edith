/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;
import java.util.List;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;

//TODO : cache rendering results
/**
 * DocumentWriter provides
 *
 * @author tiwe
 * @version $Id$
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

    void renderDocumentNotes(List<DocumentNote> documentNotes, MarkupWriter writer);

    void renderDocument(DocumentRevision document, List<DocumentNote> documentNotes,
            MarkupWriter writer) throws IOException, XMLStreamException;

}