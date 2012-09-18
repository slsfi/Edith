/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import com.mysema.edith.dto.FileItem;

/**
 * SubversionService provides Subversion client functionality
 * 
 * @author tiwe
 * @version $Id$
 */
public interface SubversionService {
    /**
     * Commits changes of a file into the repository.
     * 
     * @param svnPath
     * @param revision
     * @param username
     * @param callback
     */
    long commit(String svnPath, long revision, String username, UpdateCallback callback);

    /**
     * Delete the given svn path
     * 
     * @param svnPath
     */
    void delete(String svnPath);

    /**
     * Deletes the repository directory and related caches and working copy
     * directories.
     */
    void destroy();

    /**
     * Get directory entries
     * 
     * @param svnFolder
     * @param revision
     * @return collection of child names
     */
    Map<String, String> getEntries(String svnFolder, long revision);

    /**
     * Retrieves the latest revision number.
     * 
     * @return
     */
    long getLatestRevision();

    /**
     * Get read access to given svn path with given revision
     * 
     * @param svnPath
     *            svn path of file
     * @param revision
     * @return
     * @throws IOException
     */
    InputStream getStream(String svnPath, long revision) throws IOException;

    /**
     * Import the given file into SVN (SVN add + import)
     * 
     * @param svnPath
     *            target path
     * @param file
     *            file to be imported
     * @return revision number of commit
     */
    long importFile(String svnPath, File file);

    /**
     * Creates the repository and adds the directory structure.
     */
    void initialize();

    /**
     * @param path
     * @param revision
     * @return
     */
    List<FileItem> getFileItems(String path, int revision);

    /**
     * @param oldPath
     * @param newPath
     * @return
     */
    long move(String oldPath, String newPath);
}
