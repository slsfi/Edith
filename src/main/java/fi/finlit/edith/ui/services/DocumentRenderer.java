/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.IOException;

import javax.xml.stream.XMLStreamException;

import org.apache.tapestry5.MarkupWriter;

import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentWriter provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface DocumentRenderer {

    /**
     * @param document
     * @param writer
     * @throws Exception
     */
    // TODO : cache rendering results
    void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    /**
     * @param document
     * @param writer
     * @throws Exception
     */
    // TODO : cache rendering results
    void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

}