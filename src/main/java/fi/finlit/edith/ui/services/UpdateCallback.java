package fi.finlit.edith.ui.services;

import java.io.InputStream;
import java.io.OutputStream;

/**
 * UpdateCallback provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface UpdateCallback {
    
    /**
     * @param source
     * @param target
     */
    void update(InputStream source, OutputStream target);

}