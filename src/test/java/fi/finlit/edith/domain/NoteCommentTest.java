package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.Field;

import org.junit.Before;
import org.junit.Test;

public class NoteCommentTest {
    private NoteComment comment;
    private static final Field ID_FIELD;
    private static final String ID = "jb007";

    static {
        try {
            ID_FIELD = Identifiable.class.getDeclaredField("id");
        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        ID_FIELD.setAccessible(true);
    }

    @Before
    public void setUp() throws Exception {
        comment = new NoteComment();
        ID_FIELD.set(comment, ID);
    }

    @Test
    public void Equal_When_Same() {
        assertTrue(comment.equals(comment));
    }

    @Test
    public void Not_Equal_When_Different_Types() {
        assertFalse(comment.equals("foobar"));
    }

    @Test
    public void Equal_When_Ids_Are_Equal() throws Exception {
        NoteComment other = new NoteComment();
        ID_FIELD.set(other, ID);
        assertTrue(comment.equals(other));
    }

    @Test
    public void Not_Equal_When_Ids_Are_Not_Equal() throws Exception {
        NoteComment other = new NoteComment();
        ID_FIELD.set(other, "someotherid9204920");
        assertFalse(comment.equals(other));
    }

    @Test
    public void Not_Equal_When_Other_Is_Null() {
        NoteComment other = null;
        assertFalse(comment.equals(other));
    }

    @Test
    public void Not_Equal_When_Id_Of_Other_Not_Null() throws Exception {
        comment = new NoteComment();
        NoteComment other = new NoteComment();
        ID_FIELD.set(other, "someotherid9204920");
        assertFalse(comment.equals(other));
    }

    @Test
    public void Hash_Code_When_Id_Is_Null() {
        assertEquals(31, new NoteComment().hashCode());
    }

    @Test
    public void Hash_Code() {
        assertEquals(31 + comment.getId().hashCode(), comment.hashCode());
    }
}
