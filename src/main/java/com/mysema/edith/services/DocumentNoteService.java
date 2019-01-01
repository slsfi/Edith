/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.services;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.SelectedText;

/**
 * @author tiwe
 *
 */
public interface DocumentNoteService {
    
    /**
     * @param id
     * @return
     */
    DocumentNote getById(long id);
    
    /**
     * Attach the given 
     * 
     * @param document
     * @param selection
     * @return
     */
    DocumentNote attachNote(Document document, SelectedText selection);
    
    /**
     * Attach the given note to the given Document
     *
     * @param docRevision
     * @param selection
     * @return
     */
    DocumentNote attachNote(Note note, Document document, SelectedText selection);
    
    /**
     * Remove the given anchors from the given Document
     *
     * @param docRevision
     * @param notes
     */    
    void removeDocumentNotes(Document document, DocumentNote... notes);
        
    /**
     * Remove the DocumentNote with the given id from the related Document and flag it as deleted
     * 
     * @param id
     */
    void remove(long id);
    
    /**
     * Save or update a DocumentNote instance
     * 
     * @param documentNote
     * @return
     */
    DocumentNote save(DocumentNote documentNote);

    /**
     * Update the boundaries of the given note
     *
     * @param note
     * @param selection
     */
    DocumentNote updateNote(DocumentNote note, SelectedText selection);

}
