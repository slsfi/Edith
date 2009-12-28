package fi.finlit.edith.ui.services;

import org.apache.tapestry5.MarkupWriter;

import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentWriter provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface DocumentRenderer {

    void renderPageLinks(DocumentRevision document, MarkupWriter writer) throws Exception;
    
    void renderDocument(DocumentRevision document, MarkupWriter writer) throws Exception;

}