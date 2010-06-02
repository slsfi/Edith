package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class PlaceTest {
    @Test
    public void Hash_Code_When_Fields_Are_Null() {
        Place Place = new Place();
        assertEquals(961, Place.hashCode());
    }

    @Test
    public void Hash_Code_When_NormalizedForm_Is_Null() {
        Place Place = new Place();
        Set<NameForm> otherForms = new HashSet<NameForm>();
        Place.setOtherForms(otherForms);
        assertEquals(961 + otherForms.hashCode(), Place.hashCode());
    }

    @Test
    public void Hash_Code_When_OtherForms_Are_Null() {
        Place Place = new Place();
        NameForm normalizedForm = new NameForm();
        Place.setNormalizedForm(normalizedForm);
        assertEquals((31 + normalizedForm.hashCode()) * 31, Place.hashCode());
    }

    @Test
    public void Hash_Code() {
        Set<NameForm> otherForms = new HashSet<NameForm>();
        NameForm normalizedForm = new NameForm();
        Place Place = new Place(normalizedForm, otherForms);
        assertEquals((31 + normalizedForm.hashCode()) * 31 + otherForms.hashCode(), Place
                .hashCode());
    }

    @Test
    public void Not_Equals_When_Other_Is_Null() {
        Place Place = new Place();
        assertFalse(Place.equals(null));
    }

    @Test
    public void Not_Equals_When_Other_Is_Not_Same_Class() {
        Place Place = new Place();
        assertFalse(Place.equals("foobar"));
    }

    @Test
    public void Not_Equals_When_NormalizedForm_Is_Null_And_Others_Is_Not() {
        Place Place = new Place(null, new HashSet<NameForm>());
        Place other = new Place(new NameForm(), new HashSet<NameForm>());
        assertFalse(Place.equals(other));
    }

    @Ignore("NameForm is compared by id which cannot be changed easily.")
    @Test
    public void Not_Equals_When_NormalizedForm_Is_Different() {
        Place Place = new Place(new NameForm(), new HashSet<NameForm>());
        Place other = new Place(new NameForm(), new HashSet<NameForm>());
        assertFalse(Place.equals(other));
    }

    @Test
    public void Not_Equals_When_OtherForms_Are_Null_And_Others_Is_Not() {
        Place Place = new Place(new NameForm(), null);
        Place other = new Place(new NameForm(), new HashSet<NameForm>());
        assertFalse(Place.equals(other));
    }

    @Test
    public void Not_Equals_When_OtherForms_Differ() {
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm());
        Place Place = new Place(new NameForm(), otherForms);
        Place other = new Place(new NameForm(), new HashSet<NameForm>());
        assertFalse(Place.equals(other));
    }

    @Test
    public void Equals_When_Other_Is_The_Same_Instance() {
        Place Place = new Place();
        assertTrue(Place.equals(Place));
    }

    @Test
    public void Equals_When_Values_Equal() {
        Place Place = new Place(new NameForm(), new HashSet<NameForm>());
        Place other = new Place(new NameForm(), new HashSet<NameForm>());
        assertTrue(Place.equals(other));
    }
}
