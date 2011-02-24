package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class NameFormTest {

    private NameForm nameForm;

    @Test
    public void Get_Name_Returns_Both_Names_When_Both_Are_Set() {
        nameForm = new NameForm("Foo", "Bar", "Odd name.");
        assertEquals(nameForm.getName(), "Foo Bar");
    }

    @Test
    public void Get_Name_Returns_Just_The_First_Name_When_Only_It_Is_Set() {
        nameForm = new NameForm();
        nameForm.setFirst("Foo");
        assertEquals(nameForm.getName(), "Foo");
    }

    @Test
    public void Get_Name_Returns_Just_The_Last_Name_When_Only_It_Is_Set() {
        nameForm = new NameForm();
        nameForm.setLast("Bar");
        assertEquals(nameForm.getName(), "Bar");
    }
}
