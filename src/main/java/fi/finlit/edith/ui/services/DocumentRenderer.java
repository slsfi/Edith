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

    void render(File file, MarkupWriter writer) throws Exception;

}