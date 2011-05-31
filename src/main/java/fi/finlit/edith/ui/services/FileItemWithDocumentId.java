package fi.finlit.edith.ui.services;

import java.util.List;

import fi.finlit.edith.ui.services.svn.FileItem;

public class FileItemWithDocumentId extends FileItem {
    private final String documentId;

    public FileItemWithDocumentId(
            String title,
            String path,
            boolean isFolder,
            List<FileItem> children,
            String documentId) {
        super(title, path, isFolder, children);
        this.documentId = documentId;
    }

    public String getDocumentId() {
        return documentId;
    }
}
