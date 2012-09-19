package com.mysema.edith.services;

import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;

/**
 * @author tiwe
 *
 */
public interface DocumentNoteDao extends Dao<DocumentNote, Long> {

    /**
     * @param id
     */
    void remove(Long id);
    
    /**
     * @param docNote
     */
    void remove(DocumentNote docNote);

    /**
     * @param docNote
     * @return
     */
    DocumentNote save(DocumentNote docNote);

    /**
     * Get the note revisions of the given document revision
     * 
     * @param document
     * @param revision
     * @return
     */
    List<DocumentNote> getOfDocument(Document document);

    /**
     * Get the document notes of the given note.
     * 
     * @param noteId
     * @return
     */
    List<DocumentNote> getOfNote(Long noteId);

    /**
     * Get the DocumentNotes of the given Person.
     * 
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPerson(Long personId);

    /**
     * Get the DocumentNotes of the given Place.
     * 
     * @param personId
     * @return
     */
    List<DocumentNote> getOfPlace(Long placeId);

    /**
     * Returns all the document notes attached to the term.
     * 
     * @param termId
     * @return
     */
    List<DocumentNote> getOfTerm(Long termId);

    /**
     * @param documentRevision
     * @return
     */
    List<DocumentNote> getPublishableNotesOfDocument(Document document);

    /**
     * @param note
     * @return
     */
    int getDocumentNoteCount(Note note);

    /**
     * @param id
     * @return
     */
    long getNoteCountForDocument(Long id);

}
