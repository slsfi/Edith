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
     * @param document
     * @param revision
     * @param localId
     * @param lemma
     * @param longText
     * @return
     */
     Note createNote(DocumentRevision docRevision, String localId, String lemma, String longText);

    
    /**
     * @param file
     * @throws Exception 
     */
    int importNotes(File file) throws Exception;

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryDictionary(String searchTerm);
    
}
