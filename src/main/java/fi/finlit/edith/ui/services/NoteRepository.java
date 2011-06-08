/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteComment;

// TODO createNote and remove could be in DocumentRepository.
@Transactional
public interface NoteRepository extends Repository<Note, String> {

    /**
     * Creates a comment for the given note.
     *
     * @param concept
     * @param message
     */
    NoteComment createComment(Concept concept, String message);

    /**
     * Create a new document note for the given DocumentRevision with the given local id, lemma and long text
     *
     * @param docRevision
     * @param localId
     * @param longText
     * @return
     */
    DocumentNote createDocumentNote(Note note, DocumentRevision docRevision, String localId, String longText, int position);
    
    /**
     * A create new document not variant where local id is from current time
     * @param note
     * @param docRevision
     * @param longText
     * @return
     */
    DocumentNote createDocumentNote(Note note, DocumentRevision docRevision, String longText);
    
    /**
     * @param lemma
     * @return
     */
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

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPersons(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPlaces(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryNotes(String searchTerm);

    /**
     * @return
     */
    List<Note> getOrphans();

    /**
     * @return
     */
    List<String> getOrphanIds();

    /**
     * @param searchInfo
     * @return
     */
    List<NoteWithInstances> findNotesWithInstances(DocumentNoteSearchInfo searchInfo);

    /**
     * @param searchInfo
     * @return
     */
    List<Note> findAllNotes(DocumentNoteSearchInfo searchInfo);

    GridDataSource findNotes(DocumentNoteSearchInfo search);
    
    /**
     * @param editedNote
     */
    void save(Note editedNote);

    /**
     * Remove notes
     * 
     * @param notes
     */
    void removeNotes(Collection<Note> notes);

    /**
     * Remove note
     * 
     * @param note
     */
    void removeNote(Note note);

    /**
     * @param id
     * @return
     */
    NoteComment getCommentById(String id);




}
