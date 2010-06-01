package fi.finlit.edith.domain;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;
import java.util.Set;

import org.junit.Ignore;
import org.junit.Test;

public class NameFormsTest {
    @Test
    public void Hash_Code_When_Fields_Are_Null() {
        NameForms nameForms = new NameForms();
        assertEquals(961, nameForms.hashCode());
    }

    @Test
    public void Hash_Code_When_NormalizedForm_Is_Null() {
        NameForms nameForms = new NameForms();
        Set<NameForm> otherForms = new HashSet<NameForm>();
        nameForms.setOtherForms(otherForms);
        assertEquals(961 + otherForms.hashCode(), nameForms.hashCode());
    }

    @Test
    public void Hash_Code_When_OtherForms_Are_Null() {
        NameForms nameForms = new NameForms();
        NameForm normalizedForm = new NameForm();
        nameForms.setNormalizedForm(normalizedForm);
        assertEquals((31 + normalizedForm.hashCode()) * 31, nameForms.hashCode());
    }

    @Test
    public void Hash_Code() {
        Set<NameForm> otherForms = new HashSet<NameForm>();
        NameForm normalizedForm = new NameForm();
        NameForms nameForms = new NameForms(normalizedForm, otherForms);
        assertEquals((31 + normalizedForm.hashCode()) * 31 + otherForms.hashCode(), nameForms
                .hashCode());
    }

    @Test
    public void Not_Equals_When_Other_Is_Null() {
        NameForms nameForms = new NameForms();
        assertFalse(nameForms.equals(null));
    }

    @Test
    public void Not_Equals_When_Other_Is_Not_Same_Class() {
        NameForms nameForms = new NameForms();
        assertFalse(nameForms.equals("foobar"));
    }

    @Test
    public void Not_Equals_When_NormalizedForm_Is_Null_And_Others_Is_Not() {
        NameForms nameForms = new NameForms(null, new HashSet<NameForm>());
        NameForms other = new NameForms(new NameForm(), new HashSet<NameForm>());
        assertFalse(nameForms.equals(other));
    }

    @Ignore("NameForm is compared by id which cannot be changed easily.")
    @Test
    public void Not_Equals_When_NormalizedForm_Is_Different() {
        NameForms nameForms = new NameForms(new NameForm(), new HashSet<NameForm>());
        NameForms other = new NameForms(new NameForm(), new HashSet<NameForm>());
        assertFalse(nameForms.equals(other));
    }

    @Test
    public void Not_Equals_When_OtherForms_Are_Null_And_Others_Is_Not() {
        NameForms nameForms = new NameForms(new NameForm(), null);
        NameForms other = new NameForms(new NameForm(), new HashSet<NameForm>());
        assertFalse(nameForms.equals(other));
    }

    @Test
    public void Not_Equals_When_OtherForms_Differ() {
        Set<NameForm> otherForms = new HashSet<NameForm>();
        otherForms.add(new NameForm());
        NameForms nameForms = new NameForms(new NameForm(), otherForms);
        NameForms other = new NameForms(new NameForm(), new HashSet<NameForm>());
        assertFalse(nameForms.equals(other));
    }

    @Test
    public void Equals_When_Other_Is_The_Same_Instance() {
        NameForms nameForms = new NameForms();
        assertTrue(nameForms.equals(nameForms));
    }

    @Test
    public void Equals_When_Values_Equal() {
        NameForms nameForms = new NameForms(new NameForm(), new HashSet<NameForm>());
        NameForms other = new NameForms(new NameForm(), new HashSet<NameForm>());
        assertTrue(nameForms.equals(other));
    }
}
