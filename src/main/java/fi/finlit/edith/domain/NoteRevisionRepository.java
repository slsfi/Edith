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
    @Nullable
    NoteRevision getByLocalId(Document document, long documentRevision, String localId);

    /**
     * @param document
     * @param revision
     * @return
     */
    List<NoteRevision> getOfDocument(Document document, long revision);

}
