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
     * Creates the repository and adds the directory structure.
     */
    void initialize();

    /**
     * Deletes the repository directory.
     */
    void destroy();

    /**
     * Import the given file into SVN (SVN add + import)
     *
     * @param svnPath target path
     * @param file file to be imported
     * @return revision number of commit
     */
    long importFile(String svnPath, File file);

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
     * Update the contents of the file. Works only on files that are checked out
     * first.
     *
     * TODO Throw checked exception signaling update failure.
     *
     * @param file
     */
    void update(File file);

    /**
     * Commit the given file. Works only on files that are checked out first.
     *
     * TODO Throw checked exception signaling commit failure instead of RuntimeException.
     *
     * @param file
     * @return
     */
    long commit(File file);

    /**
     * Retrieves the latest revision number.
     *
     * @return
     */
    long getLatestRevision();

    /**
     * Checks out a directory into the destination folder, svnPath refers to the
     * SVN file's parent directory.
     *
     * @param destination
     * @param svnPath
     * @param revision
     */
    void checkout(File destination, String svnPath, long revision);

}
