package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import org.tmatesoft.svn.core.SVNException;

/**
 * SubversionService provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {
    
    void commit(String svnPath, File file) throws SVNException;
    
    File getFile(String svnPath, long revision) throws IOException, SVNException;
    
    void remove(String svnPath);

    List<Long> getRevisions(String svnPath) throws SVNException;

    Collection<String> getEntries(String svnFolder, int revision) throws SVNException;

}
