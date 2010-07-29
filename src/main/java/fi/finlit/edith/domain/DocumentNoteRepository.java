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

import fi.finlit.edith.ui.services.DocumentNoteSearchInfo;

/**
 * The Interface NoteRevisionRepository.
 */
@Transactional
public interface DocumentNoteRepository extends Repository<DocumentNote, String> {

    /**
     * Get the NoteRevision with the given local id in the scope of the given document revision
     *
     * @param document
     * @param documentRevision
     * @param localId
     * @return
     */
    @Nullable
    DocumentNote getByLocalId(DocumentRevision docRevision, String localId);

    /**
     * Get the note revisions of the given document revision
     *
     * @param document
     * @param revision
     * @return
     */
    List<DocumentNote> getOfDocument(DocumentRevision docRevision);

    /**
     * Query for notes matching the given search term
     *
     * @param searchTem
     * @return
     */
    GridDataSource queryNotes(String searchTem);
    
    List<DocumentNote> query(DocumentNoteSearchInfo searchInfo);

}
