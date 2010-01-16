/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

/**
 * The Interface NoteRevisionRepository.
 */
@Transactional
public interface NoteRevisionRepository extends Repository<NoteRevision, String> {

    /**
     * @param searchTem
     * @return
     */
    GridDataSource queryNotes(String searchTem);

    /**
     * @param document
     * @param documentRevision
     * @param localId
     * @return
     */
    NoteRevision getByLocalId(Document document, long documentRevision, String localId);

}
