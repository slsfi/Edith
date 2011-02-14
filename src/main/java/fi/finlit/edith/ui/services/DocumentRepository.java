/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

@Transactional
public interface DocumentRepository extends Repository<Document, String> {

    /**
     * Import the given File to the given svnPath
     *
     * @param svnPath
     * @param file
     */
    void addDocument(String svnPath, File file);

    /**
     * Add the given note for the given Document
     *
     * @param docRevision
     * @param selection
     * @return
     * @throws IOException
     * @throws NoteAdditionFailedException
     */
    DocumentNote addNote(Note note, DocumentRevision docRevision, SelectedText selection) throws IOException, NoteAdditionFailedException;

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
    InputStream getDocumentStream(DocumentRevision docRevision) throws IOException;

    /**
     * Get the SVN revisions for the given document in ascending order
     *
     * @param document
     * @return
     */
    List<RevisionInfo> getRevisions(Document document);

    /**
     * Remove all notes from the given Document
     *
     * @param document
     * @return
     * @throws IOException
     */
    DocumentRevision removeAllNotes(Document document);

    /**
     * Remove the given anchors from the given Document
     *
     * @param docRevision
     * @param notes
     * @throws IOException
     */
    DocumentRevision removeNotes(DocumentRevision docRevision, DocumentNote... notes);

    /**
     * Permanently removes document notes and their anchors.
     *
     * @param docRevision
     * @param notes
     * @return
     */
    DocumentRevision removeNotesPermanently(DocumentRevision docRevision, DocumentNote... notes);

    /**
     * Update the boundaries of the given note
     *
     * @param note
     * @param selection
     * @throws IOException
     */
    DocumentNote updateNote(DocumentNote note, SelectedText selection) throws IOException;

}
