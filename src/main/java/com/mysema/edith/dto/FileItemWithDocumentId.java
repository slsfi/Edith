/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.List;

public class FileItemWithDocumentId extends FileItem {
    private final Long documentId;

    private final boolean isSelected;

    private final long noteCount;

    private final Long lastNote;

    private final String href;

    public FileItemWithDocumentId(String title, String path, boolean isFolder,
            List<FileItem> children, boolean hasChildren, Long documentId, boolean isSelected,
            long noteCount, Long lastNote) {
        super(title, path, isFolder, children, hasChildren);
        this.documentId = documentId;
        this.isSelected = isSelected;
        this.noteCount = noteCount;
        this.lastNote = lastNote;
        href = "#documents/" + documentId;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public boolean getIsSelected() {
        return isSelected;
    }

    public long getNoteCount() {
        return noteCount;
    }

    public Long getLastNote() {
        return lastNote;
    }

    public String getHref() {
        return href;
    }
}
