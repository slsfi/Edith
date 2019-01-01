/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.dto;

public class SelectionTO {
    
    private Long noteId;
    
    private SelectedText text;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public SelectedText getText() {
        return text;
    }

    public void setText(SelectedText text) {
        this.text = text;
    }
    
}
