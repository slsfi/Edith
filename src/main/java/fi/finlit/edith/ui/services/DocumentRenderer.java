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

//TODO : cache rendering results
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
    void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

    /**
     * @param document
     * @param writer
     * @throws Exception
     */
    void renderDocument(DocumentRevision document, MarkupWriter writer) throws IOException, XMLStreamException;

}