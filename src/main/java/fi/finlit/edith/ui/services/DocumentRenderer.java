package fi.finlit.edith.ui.services;

import java.io.File;

import org.apache.tapestry5.MarkupWriter;

/**
 * DocumentWriter provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface DocumentRenderer {

    /**
     * @param file
     * @param writer
     * @throws Exception
     */
    void renderPageLinks(File file, MarkupWriter writer) throws Exception;
    
    /**
     * @param file
     * @param writer
     * @throws Exception
     */
    void renderDocument(File file, MarkupWriter writer) throws Exception;

}