package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

public class SelectedTextTest {
    private static final String START_ID = "start";
    private static final String END_ID = "end";
    private static final String SELECTION = "lots of text";

    private SelectedText selectedText;

    @Before
    public void Before() {
        selectedText = new SelectedText(START_ID, END_ID, SELECTION);
    }

    @Test
    public void Get_End_Id() {
        assertEquals(END_ID, selectedText.getEndId());
    }

    @Test
    public void Get_End_Index() {
        assertEquals(1, selectedText.getEndIndex());
    }

    @Test
    public void Get_Selection() {
        assertEquals(SELECTION, selectedText.getSelection());
    }

    @Test
    public void Get_Start_Id() {
        assertEquals(START_ID, selectedText.getStartId());
    }

    @Test
    public void Get_Start_Index() {
        assertEquals(1, selectedText.getStartIndex());
    }

    @Test
    public void Is_Valid() {
        assertTrue(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_Selection_Is_Null() {
        selectedText = new SelectedText(START_ID, END_ID, null);
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_Selection_Is_Empty() {
        selectedText = new SelectedText(START_ID, END_ID, "");
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_Start_Id_Is_Null() {
        selectedText = new SelectedText(null, END_ID, SELECTION);
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_Start_Id_Is_Empty() {
        selectedText = new SelectedText("", END_ID, SELECTION);
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_End_Id_Is_Null() {
        selectedText = new SelectedText(START_ID, null, SELECTION);
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Is_Not_Valid_When_End_Id_Is_Empty() {
        selectedText = new SelectedText(START_ID, "", SELECTION);
        assertFalse(selectedText.isValid());
    }

    @Test
    public void Get_First_Word() {
        assertEquals("lots", selectedText.getFirstWord());
    }

    @Test
    public void Get_Last_Word() {
        assertEquals("text", selectedText.getLastWord());
    }

    @Test
    public void Start_Is_Child_Of_End() {
        selectedText = new SelectedText("a-b-c", "a-b", "text");
        assertTrue(selectedText.isStartChildOfEnd());
    }

    @Test
    public void Start_Is_Not_Child_Of_End() {
        selectedText = new SelectedText("a-b", "a-c", "text");
        assertFalse(selectedText.isStartChildOfEnd());
    }

    @Test
    public void How_Deep_Is_Start_In_End() {
        selectedText = new SelectedText("a-b-c", "a-b", "text");
        assertEquals(1, selectedText.howDeepIsStartInEnd());
    }

    @Test
    public void How_Deep_Is_Start_In_End_Start_Is_Not_In_End() {
        selectedText = new SelectedText("a-c", "a-b", "text");
        assertEquals(-1, selectedText.howDeepIsStartInEnd());
    }

    @Test
    public void How_Deep_Is_End_In_Start() {
        selectedText = new SelectedText("a-b", "a-b-c", "text");
        assertEquals(1, selectedText.howDeepIsEndInStart());
    }

    @Test
    public void How_Deep_Is_End_In_Start_End_Is_Not_In_Start() {
        selectedText = new SelectedText("a-c", "a-b", "text");
        assertEquals(-1, selectedText.howDeepIsEndInStart());
    }

    @Test
    public void End_Is_Child_Of_Start() {
        selectedText = new SelectedText("a-b", "a-b-c", "text");
        assertTrue(selectedText.isEndChildOfStart());
    }

    @Test
    public void End_Is_Not_Child_Of_Start() {
        selectedText = new SelectedText("a-b", "a-b", "text");
        assertFalse(selectedText.isEndChildOfStart());
    }

    @Test
    public void To_String() {
        assertEquals(START_ID + "[1] , " + END_ID + "[1] : " + SELECTION, selectedText.toString());
    }
}
