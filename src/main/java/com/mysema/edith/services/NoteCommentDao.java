/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.services;

import java.util.List;

import com.mysema.edith.domain.NoteComment;

/**
 * @author tiwe
 *
 */
public interface NoteCommentDao extends Dao<NoteComment, Long> {

    /**
     * @param noteId
     * @return
     */
    List<NoteComment> getOfNote(long noteId);
    
    /**
     * @param noteId
     * @return
     */
    NoteComment getOneOfNote(long noteId);
    
    /**
     * @param comment
     * @return
     */
    NoteComment save(NoteComment comment);
    
    /**
     * @param comment
     */
    void remove(NoteComment comment);
    
    /**
     * @param comment
     */
    void remove(Long comment);
}
