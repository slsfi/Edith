/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.dto.SelectedText;

/**
 * @author tiwe
 *
 */
public interface DocumentDao extends Dao<Document, Long> {

    /**
     * Import the given File to the given svnPath
     *
     * @param svnPath
     * @param file
     */
    void addDocument(String svnPath, File file);

    /**
     * Import documents from the given ZIP file
     *
     * @param parentSvnPath
     * @param file
     * @return amount of imported documents
     */
    int addDocumentsFromZip(String parentSvnPath, File file);

    /**
     * Add the given note for the given Document
     *
     * @param docRevision
     * @param selection
     * @return
     * @throws IOException
     * @throws NoteAdditionFailedException
     */
    DocumentNote addNote(Note note, Document document, SelectedText selection) throws IOException,
            NoteAdditionFailedException;

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
     * Remove the given anchors from the given Document
     *
     * @param docRevision
     * @param notes
     * @throws IOException
     */
    @Deprecated
    // Move to service
    void removeDocumentNotes(Document document, DocumentNote... notes);

    /**
     * Update the boundaries of the given note
     *
     * @param note
     * @param selection
     * @throws IOException
     */
    @Deprecated
    // Move to service
    DocumentNote updateNote(DocumentNote note, SelectedText selection) throws IOException;

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
     * Remove the given documents
     *
     * @param documents
     */
    void removeAll(Collection<Document> documents);

    /**
     * @param id
     * @param newPath
     */
    void rename(Long id, String newPath);

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

    List<NoteComment> getNoteComments(long id, long limit);
}
