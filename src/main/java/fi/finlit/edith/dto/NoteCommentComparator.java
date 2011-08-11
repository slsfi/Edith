package fi.finlit.edith.dto;

import java.util.Comparator;

import fi.finlit.edith.sql.domain.NoteComment;

public final class NoteCommentComparator implements Comparator<NoteComment> {

    public static final NoteCommentComparator ASC = new NoteCommentComparator();

    @Override
    public int compare(NoteComment o1, NoteComment o2) {
        if (o1.getCreatedAt().equals(o2.getCreatedAt())) {
            return 0;
        }
        return o1.getCreatedAt().isBefore(o2.getCreatedAt()) ? -1 : 1;
    }

}
