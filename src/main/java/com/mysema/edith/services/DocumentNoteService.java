package com.mysema.edith.services;

import java.io.IOException;

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
     * Attach the given note to the given Document
     *
     * @param docRevision
     * @param selection
     * @return
     * @throws IOException
     * @throws NoteAdditionFailedException
     */
    DocumentNote attachNote(Note note, Document document, SelectedText selection) throws IOException,
            NoteAdditionFailedException;
    
    /**
     * Remove the given anchors from the given Document
     *
     * @param docRevision
     * @param notes
     * @throws IOException
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
     * @throws IOException
     */
    DocumentNote updateNote(DocumentNote note, SelectedText selection) throws IOException;

}
