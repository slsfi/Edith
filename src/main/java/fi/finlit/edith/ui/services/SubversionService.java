/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

/**
 * SubversionService provides Subversion client functionality
 *
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {
    /**
     * Delete the given svn path
     *
     * @param svnPath
     */
    void delete(String svnPath);

    /**
     * Deletes the repository directory and related caches and working copy directories.
     */
    void destroy();

    /**
     * Get directory entries
     *
     * @param svnFolder
     * @param revision
     * @return collection of child names
     */
    Collection<String> getEntries(String svnFolder, long revision);

    /**
     * Get read access to given svn path with given revision
     *
     * @param svnPath svn path of file
     * @param revision
     * @return
     * @throws IOException
     */
    InputStream getStream(String svnPath, long revision) throws IOException;

    /**
     * Retrieves the latest revision number.
     *
     * @return
     */
    long getLatestRevision();

    /**
     * Get revisions of given path
     *
     * @param svnPath
     * @return
     */
    List<Long> getRevisions(String svnPath);

    /**
     * Import the given file into SVN (SVN add + import)
     *
     * @param svnPath target path
     * @param file file to be imported
     * @return revision number of commit
     */
    long importFile(String svnPath, File file);

    /**
     * Creates the repository and adds the directory structure.
     */
    void initialize();

    /**
     *
     * TODO Apparently returns -1 at least when there are no changes.
     *
     * @param svnPath
     * @param revision
     * @param callback
     */
    long commit(String svnPath, long revision, UpdateCallback callback);

}
