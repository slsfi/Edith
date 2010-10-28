package fi.finlit.edith.domain;

import java.util.List;

public class Notes {
    private final List<Note> orphans;
    private final List<DocumentNote> documentNotes;

    public Notes(List<Note> orphans, List<DocumentNote> documentNotes) {
        this.orphans = orphans;
        this.documentNotes = documentNotes;
    }

    public List<DocumentNote> getDocumentNotes() {
        return documentNotes;
    }

    public List<Note> getOrphans() {
        return orphans;
    }
}
