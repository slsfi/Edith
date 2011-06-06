package fi.finlit.edith.ui.services;

import java.util.List;

import fi.finlit.edith.ui.services.svn.FileItem;

public class FileItemWithDocumentId extends FileItem {
    private final String documentId;

    private final boolean isSelected;

    public FileItemWithDocumentId(
            String title,
            String path,
            boolean isFolder,
            List<FileItem> children,
            String documentId,
            boolean isSelected) {
        super(title, path, isFolder, children);
        this.documentId = documentId;
        this.isSelected = isSelected;
    }

    public String getDocumentId() {
        return documentId;
    }

    public boolean isSelected() {
        return isSelected;
    }
}
