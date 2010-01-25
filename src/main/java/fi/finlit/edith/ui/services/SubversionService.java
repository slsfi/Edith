/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

/**
 * SubversionService provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {
    
    /**
     * Import the given file into SVN (SVN add + import)
     * 
     * @param svnPath target path
     * @param file file to be imported
     */
    void importFile(String svnPath, File file);
    
    /**
     * Get directory entries
     * 
     * @param svnFolder
     * @param revision
     * @return collection of child names
     */
    Collection<String> getEntries(String svnFolder, long revision);
    
    /**
     * Get file access to given svn path with given revision
     * 
     * @param svnPath svn path of file
     * @param revision 
     * @return
     * @throws IOException
     */
    File getFile(String svnPath, long revision) throws IOException;

    /**
     * Get revisions of given path
     * 
     * @param svnPath
     * @return
     */
    List<Long> getRevisions(String svnPath);

    /**
     * Delete the given svn path
     * 
     * @param svnPath
     */
    void delete(String svnPath);

    /**
     * Update the contents from the given svn path to file
     * 
     * @param svnPath
     * @param file
     */
    void update(String svnPath, File file);

    /**
     * Commit the given file to the given svnPath
     * 
     * @param svnPath
     * @param file
     * @return
     */
    long commit(String svnPath, File file);

}
