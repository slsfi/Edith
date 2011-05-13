package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;

import org.joda.time.DateTime;
import org.junit.Test;

public class NoteCommentComparatorTest {

    @Test
    public void Compare() {
        NoteComment comment1 = new NoteComment();
        NoteComment comment2 = new NoteComment();
        comment1.setCreatedAt(new DateTime());        
        comment2.setCreatedAt(new DateTime().minusDays(1));
        
        assertEquals(1, NoteCommentComparator.ASC.compare(comment1, comment2));
        assertEquals(-1,  NoteCommentComparator.DESC.compare(comment1, comment2));
    }
    
    @Test
    public void Compare__Same_Date() {
        DateTime dateTime = new DateTime();
        NoteComment comment1 = new NoteComment();
        NoteComment comment2 = new NoteComment();
        comment1.setCreatedAt(dateTime);        
        comment2.setCreatedAt(dateTime);
        
        assertEquals(0, NoteCommentComparator.ASC.compare(comment1, comment2));
        assertEquals(0, NoteCommentComparator.DESC.compare(comment1, comment2));
    }


}
