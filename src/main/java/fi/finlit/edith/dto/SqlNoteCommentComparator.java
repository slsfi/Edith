package fi.finlit.edith.dto;

import java.util.Comparator;

import fi.finlit.edith.sql.domain.NoteComment;

public final class SqlNoteCommentComparator implements Comparator<NoteComment> {

    public static final SqlNoteCommentComparator ASC = new SqlNoteCommentComparator(true);
    
    public static final SqlNoteCommentComparator DESC = new SqlNoteCommentComparator(false);

    private final boolean asc;
    
    private SqlNoteCommentComparator(boolean asc) {
        this.asc = asc;
    }
    
    @Override
    public int compare(NoteComment o1, NoteComment o2) {
        if (o1.getCreatedAt().equals(o2.getCreatedAt())) {
            return 0;
        } else {
            return o1.getCreatedAt().isBefore(o2.getCreatedAt()) ? (asc ? -1 : 1) : (asc ? 1 : -1); 
        } 
    }
    
}
