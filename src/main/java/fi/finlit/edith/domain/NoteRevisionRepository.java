/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.domain;

import java.util.List;

import javax.annotation.Nullable;

import org.apache.tapestry5.grid.GridDataSource;
import org.springframework.transaction.annotation.Transactional;

import com.mysema.rdfbean.dao.Repository;

/**
 * The Interface NoteRevisionRepository.
 */
@Transactional
public interface NoteRevisionRepository extends Repository<NoteRevision, String> {

    /**
     * @param document
     * @param documentRevision
     * @param localId
     * @return
     */
    @Nullable
    NoteRevision getByLocalId(DocumentRevision docRevision, String localId);

    /**
     * @param document
     * @param revision
     * @return
     */
    List<NoteRevision> getOfDocument(DocumentRevision docRevision);

    /**
     * @param searchTem
     * @return
     */
    GridDataSource queryNotes(String searchTem);

}
