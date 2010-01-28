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
 * SubversionService provides
 *
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {

//    /**
//     * Checks out a directory into the destination folder, svnPath refers to the
//     * SVN file's parent directory.
//     *
//     * @param destination
//     * @param svnPath
//     * @param revision
//     */
//    void checkout(File destination, String svnPath, long revision);

//    /**
//     * Commit the given file. Works only on files that are checked out first.
//     *
//     * TODO Throw checked exception signaling commit failure instead of RuntimeException.
//     *
//     * @param file
//     * @return
//     */
//    long commit(File file);

    /**
     * Delete the given svn path
     *
     * @param svnPath
     */
    void delete(String svnPath);

    /**
     * Deletes the repository directory.
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

//    /**
//     * Update the contents of the file. Works only on files that are checked out
//     * first.
//     *
//     * TODO Throw checked exception signaling update failure.
//     *
//     * @param file
//     */
//    void update(File file);
    
    /**
     * @param svnPath
     * @param revision
     * @param callback
     */
    void commit(String svnPath, long revision, UpdateCallback callback);

}
