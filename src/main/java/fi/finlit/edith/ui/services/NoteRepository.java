/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;
import fi.finlit.edith.domain.Notes;

// TODO createNote and remove could be in DocumentRepository.
/**
 * NoteRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface NoteRepository extends Repository<Note, String> {

    /**
     * Creates a comment for the given note.
     *
     * @param note
     * @param message
     */
    NoteComment createComment(Note note, String message);

    /**
     * Create a new Note for the given DocumentRevision with the given local id, lemma and long text
     *
     * @param docRevision
     * @param localId
     * @param longText
     * @return
     */
    DocumentNote createDocumentNote(Note note, DocumentRevision docRevision, String localId, String longText);

    Note find(String lemma);

    /**
     * Import notes from the given file
     *
     * @param file
     * @throws Exception
     */
    int importNotes(File file);

    /**
     * Query for notes with the given search term
     *
     * @param searchTerm
     * @return
     */
    GridDataSource queryDictionary(String searchTerm);

    /**
     * Remove the give Note in the given revision. This don't remove data, it just set DocumentNote to a
     * deleted state
     *
     * @param note
     * @param newRevision
     */
    void remove(DocumentNote note, long revision);

    /**
     * Removes the document note permanently
     * 
     * @param note
     */
    void removePermanently(DocumentNote note);
    
    /**
     * Removes a NoteComment based on its id. Returns the deleted comment.
     *
     * @param commentId
     * @return
     */
    NoteComment removeComment(String commentId);

    /**
     * Finds all the Notes based on a lemma.
     * @param lemma
     * @return
     */
    List<Note> findNotes(String lemma);

    GridDataSource queryPersons(String searchTerm);

    GridDataSource queryPlaces(String searchTerm);
    

    // TODO TEST
    List<Note> getOrphans();

    /**
     * @param searchInfo
     * @return
     */
    Notes query(DocumentNoteSearchInfo searchInfo);
}
