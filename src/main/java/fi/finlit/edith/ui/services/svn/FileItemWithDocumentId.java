package fi.finlit.edith.ui.services.svn;

import java.util.List;


public class FileItemWithDocumentId extends FileItem {
    private final Long documentId;

    private final boolean isSelected;

    private final long noteCount;

    public FileItemWithDocumentId(
            String title,
            String path,
            boolean isFolder,
            List<FileItem> children,
            boolean hasChildren,
            Long documentId,
            boolean isSelected,
            long noteCount) {
        super(title, path, isFolder, children, hasChildren);
        this.documentId = documentId;
        this.isSelected = isSelected;
        this.noteCount = noteCount;
    }

    public Long getDocumentId() {
        return documentId;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public long getNoteCount() {
        return noteCount;
    }
}
