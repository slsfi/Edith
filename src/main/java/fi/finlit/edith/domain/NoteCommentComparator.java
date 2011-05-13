package fi.finlit.edith.domain;

import java.util.Comparator;

public final class NoteCommentComparator implements Comparator<NoteComment> {

    public static final NoteCommentComparator ASC = new NoteCommentComparator(true);
    
    public static final NoteCommentComparator DESC = new NoteCommentComparator(false);

    private final boolean asc;
    
    private NoteCommentComparator(boolean asc) {
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
