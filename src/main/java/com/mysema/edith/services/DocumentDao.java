/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.dto.FileItemWithDocumentId;

/**
 * @author tiwe
 *
 */
public interface DocumentDao extends Dao<Document, Long> {

    /**
     * Import the given File to the given path
     *
     * @param path
     * @param file
     */
    Document addDocument(String path, File file);

    /**
     * Import documents from the given ZIP file
     *
     * @param parentSvnPath
     * @param file
     * @return amount of imported documents
     */
    List<Document> addDocumentsFromZip(String parentSvnPath, File file);

    /**
     * Get a Document handle for the given path
     *
     * @param svnPath
     * @return
     */
    Document getDocumentForPath(String svnPath);

    /**
     * Get the Documents of the given directory path and its subpaths
     *
     * @param svnFolder
     * @return
     */
    List<Document> getDocumentsOfFolder(String svnFolder);

    /**
     * Get the file for the given document for reading
     *
     * @param docRevision
     * @return
     * @throws IOException
     */
    InputStream getDocumentStream(Document document) throws IOException;

    /**
     * Remove the given document
     *
     * @param doc
     */
    void remove(Document doc);

    /**
     * Remove the document by id.
     */
    void remove(Long id);

    /**
     * @param path
     */
    void removeByPath(String path);

    /**
     * Remove the given documents
     *
     * @param documents
     */
    void removeAll(Collection<Document> documents);

    /**
     * @param id
     * @param newPath
     */
    Document rename(Long id, String newPath);

    /**
     * @param path
     * @param id
     * @return
     */
    List<FileItemWithDocumentId> fromPath(String path, Long id);

    /**
     * @param doc
     */
    Document save(Document doc);

    /**
     * @param id
     * @param limit
     * @return
     */
    List<NoteComment> getNoteComments(long id, long limit);

}
