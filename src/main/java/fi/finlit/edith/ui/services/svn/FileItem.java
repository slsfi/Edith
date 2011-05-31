package fi.finlit.edith.ui.services.svn;

import java.util.List;

public class FileItem {
    private final String title;
    private final String path;
    private final boolean isFolder;
    private final boolean isLazy;
    private final List<FileItem> children;

    public FileItem(
            String title,
            String path,
            boolean isFolder,
            List<FileItem> children) {
        this.title = title;
        this.path = path;
        this.isFolder = isFolder;
        this.isLazy = isFolder;
        this.children = children;
    }

    public List<FileItem> getChildren() {
        return children;
    }

    public String getTitle() {
        return title;
    }

    public boolean isFolder() {
        return isFolder;
    }

    public boolean isLazy() {
        return isLazy;
    }

    public String getPath() {
        return path;
    }

}
