/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

/**
 * The Interface NoteRevisionRepository.
 */
@Transactional
public interface DocumentNoteRepository extends Repository<DocumentNote, String> {

    /**
     * Get the NoteRevision with the given local id in the scope of the given document revision
     *
     * @param document
     * @param documentRevision
     * @param localId
     * @return
     */
    @Nullable
    DocumentNote getByLocalId(DocumentRevision docRevision, String localId);

    /**
     * Get the note revisions of the given document revision
     *
     * @param document
     * @param revision
     * @return
     */
    List<DocumentNote> getOfDocument(DocumentRevision docRevision);

    /**
     * Get the document notes of the given note.
     *
     * @param noteId
     * @return
     */
    List<DocumentNote> getOfNote(String noteId);

    /**
     * Get the DocumentNotes of the given Person.
     *
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPerson(String personId);

    /**
     * Get the DocumentNotes of the given Place.
     *
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPlace(String placeId);

    /**
     * Query for notes matching the given search term
     *
     * @param searchTem
     * @return
     */
    GridDataSource queryNotes(String searchTem);

    /**
     * @param searchInfo
     * @return
     */
    List<DocumentNote> query(DocumentNoteSearchInfo searchInfo);

    /**
     * Returns all the document notes attached to the term.
     *
     * @param termId
     * @return
     */
    List<DocumentNote> getOfTerm(String termId);

    /**
     * Saves the document note with the backing note copied as a new one.
     *
     * @param docNote
     * @return
     */
    DocumentNote saveAsCopy(DocumentNote docNote);

    /**
     * Returns the DocumentNotes with the same given Note in the given Document.
     *
     * @param noteId
     * @param documentId
     * @return
     */
    List<DocumentNote> getOfNoteInDocument(String noteId, String documentId);

    /**
     * Removes the orphan DocumentNotes of the given Note.
     *
     * @param noteId
     */
    void removeOrphans(String noteId);

    List<DocumentNote> getPublishableNotesOfDocument(DocumentRevision documentRevision);

}
