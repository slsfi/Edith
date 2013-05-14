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
     * Add the given note for the given Document
     *
     * @param docRevision
     * @param selection
     * @return
     * @throws IOException
     * @throws NoteAdditionFailedException
     */
    DocumentNote addNote(Note note, Document document, SelectedText selection) throws IOException,
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
     * Update the boundaries of the given note
     *
     * @param note
     * @param selection
     * @throws IOException
     */
    DocumentNote updateNote(DocumentNote note, SelectedText selection) throws IOException;

}
