/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.io.File;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

/**
 * NoteRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface NoteRepository extends Repository<Note,String>{
    
    /**
     * Create a new Note for the given DocumentRevision with the given local id, lemma and long text
     * 
     * @param docRevision
     * @param localId
     * @param lemma
     * @param longText
     * @return
     */
     Note createNote(DocumentRevision docRevision, String localId, String lemma, String longText);

    
    /**
     * Import notes from the given file
     * 
     * @param file
     * @throws Exception 
     */
     @Deprecated
    int importNotes(File file) throws Exception;

    /**
     * Query for notes with the given search term
     * 
     * @param searchTerm
     * @return
     */
    GridDataSource queryDictionary(String searchTerm);

    /**
     * Remove the give Note in the given revision
     * 
     * @param note
     * @param newRevision
     */
    void remove(Note note, long revision);
    
}
