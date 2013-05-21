/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.List;

public class FileItem {
    private final String title;
    private final String path;
    private final boolean isFolder;
    private final boolean isLazy;
    private final List<FileItem> children;
    private final boolean hasChildren;

    public FileItem(String title, String path, boolean isFolder, List<FileItem> children,
            boolean hasChildren) {
        this.title = title;
        this.path = path;
        this.isFolder = isFolder;
        this.isLazy = isFolder;
        this.children = children;
        this.hasChildren = hasChildren;
    }

    public List<FileItem> getChildren() {
        return children;
    }

    public String getTitle() {
        return title;
    }

    public boolean getIsFolder() {
        return isFolder;
    }

    public boolean getIsLazy() {
        return isLazy;
    }

    public String getPath() {
        return path;
    }

    public boolean getHasChildren() {
        return hasChildren;
    }

}
