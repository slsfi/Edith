/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.services;

import java.io.InputStream;
import java.io.OutputStream;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.dto.SelectedText;

/**
 * @author tiwe
 *
 */
public interface DocumentXMLDao {

    /**
     * @param source
     * @param target
     * @param selection
     * @param documentNote
     * @return
     */
    public abstract int addNote(InputStream source, OutputStream target, SelectedText selection,
            DocumentNote documentNote);

    /**
     * @param source
     * @param target
     * @param documentNotes
     */
    public abstract void removeNotes(InputStream source, OutputStream target,
            DocumentNote... documentNotes);

    /**
     * @param source
     * @param target
     * @param selection
     * @param documentNote
     * @return
     */
    public abstract int updateNote(InputStream source, OutputStream target, SelectedText selection,
            DocumentNote documentNote);

}