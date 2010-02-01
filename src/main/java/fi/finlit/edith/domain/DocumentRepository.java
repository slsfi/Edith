/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

/**
 * DocumentRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface DocumentRepository extends Repository<Document,String>{

    /**
     * Import the given File to the given svnPath
     *
     * @param string
     * @param file
     */
    void addDocument(String svnPath, File file);

    /**
     * Add the given note for the given Document
     *
     * @param document
     * @param svnRevision
     * @param startId
     * @param endId
     * @param text
     * @return
     * @throws IOException
     */
    Note addNote(Document document, long svnRevision, String startId, String endId, String text) throws IOException;

    /**
     * Update the boundaries of the given note in the context of the given document
     * 
     * @param document
     * @param note
     * @param startId
     * @param endId
     * @param text
     * @throws IOException
     */
    void updateNote(Document document, NoteRevision note, String startId, String endId, String text) throws IOException;
    
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
     * @param svnPath
     * @param revision
     * @return
     * @throws IOException
     */
    InputStream getDocumentStream(DocumentRevision document) throws IOException;

    /**
     * Get the SVN revisions for the given document in ascending order
     *
     * @param document
     * @return
     */
    List<Long> getRevisions(Document document);

    /**
     * Remove the given anchors from the given Document
     *
     * @param document
     * @param svnRevision
     * @param anchors
     * @throws IOException
     */
    void removeNoteAnchors(Document document, long svnRevision, Note... anchors) throws IOException;

}
