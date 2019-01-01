/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import java.io.File;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.query.SearchResults;

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
    
    /**
     * @param stream
     * @return
     */
    int importNotes(InputStream stream);


    /**
     * Removes a NoteComment based on its id. Returns the deleted comment.
     *
     * @param commentId
     * @return
     */
    NoteComment removeComment(Long commentId);

    /**
     * @return
     */
    List<Long> getOrphanIds();

    /**
     * @param search
     * @return
     */
    SearchResults<DocumentNote> findDocumentNotes(NoteSearchTO search);
    
    /**
     * 
     * @param search
     * @param modifiers
     * @return
     */
    SearchResults<Note> findNotes(NoteSearchTO search);
    
    /**
     * @param editedNote
     */
    Note save(Note editedNote);

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
    void saveAsNew(Note note);
}
