/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

/**
 * The Interface NoteRevisionRepository.
 */
@Transactional
public interface NoteRevisionRepository extends Repository<NoteRevision, String> {

    
    
    GridDataSource queryNotes(String searchTem);

}
