/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;

/**
 * @author tiwe
 *
 */
public interface NoteDao extends Dao<Note, Long> {

    /**
     * Creates a comment for the given note.
     * 
     * @param concept
     * @param message
     */
    NoteComment createComment(Note note, String message);

    /**
     * @param documentNote
     * @param note
     * @param document
     * @param longText
     * @param position
     * @return
     */
    DocumentNote createDocumentNote(DocumentNote documentNote, Note note, Document document,
            String longText, int position);

    /**
     * Import notes from the given file
     * 
     * @param file
     * @throws Exception
     */
    int importNotes(File file);

//    /**
//     * Query for notes with the given search term
//     * 
//     * @param searchTerm
//     * @return
//     */
//    GridDataSource queryDictionary(String searchTerm);

    /**
     * Removes a NoteComment based on its id. Returns the deleted comment.
     * 
     * @param commentId
     * @return
     */
    NoteComment removeComment(Long commentId);

//    /**
//     * @param searchTerm
//     * @return
//     */
//    GridDataSource queryPersons(String searchTerm);
//
//    /**
//     * @param searchTerm
//     * @return
//     */
//    GridDataSource queryPlaces(String searchTerm);
//
//    /**
//     * @param searchTerm
//     * @return
//     */
//    GridDataSource queryNotes(String searchTerm);

    /**
     * @return
     */
    List<Long> getOrphanIds();

//    /**
//     * @param search
//     * @return
//     */
//    GridDataSource findNotes(NoteSearchInfo search);

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
    void remove(Note note);
    
    /**
     * @param id
     */
    void remove(Long id);

    /**
     * @param note
     * @param document
     * @param longText
     * @return
     */
    DocumentNote createDocumentNote(Note note, Document document, String longText);

    /**
     * @param noteOnEdit
     */
    void saveAsNew(Note noteOnEdit);
}
