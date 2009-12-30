/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.io.File;

import org.springframework.transaction.annotation.Transactional;

import com.mysema.query.paging.ListSource;

/**
 * NoteRepository provides
 *
 * @author tiwe
 * @version $Id$
 */
@Transactional
public interface NoteRepository extends Repository<Note,String>{
    
    /**
     * @param searchTerm
     * @return
     */
    ListSource<NoteRevision> queryNotes(String searchTerm);

    /**
     * @param file
     * @throws Exception 
     */
    void importNotes(File file) throws Exception;

}
