package fi.finlit.edith.ui.mixins;

import static org.junit.Assert.assertEquals;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermLanguage;

public class TermAutocompleteTest {
    private TermAutocomplete autocomplete;

    @Before
    public void setUp() {
        autocomplete = new TermAutocomplete();
    }

    @Test
    public void Generate_Response_One_Element_Found_And_Meaning_Is_Set() throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        List<Object> terms = new ArrayList<Object>();
        Field field = Term.class.getSuperclass().getDeclaredField("id");
        field.setAccessible(true);
        Term term = new Term();
        field.set(term, "111");
        term.setBasicForm("kuusi");
        term.setLanguage(TermLanguage.FINNISH);
        term.setMeaning("Tarkoittaa puuta. Eli ei numeroa, vaan puuta.");
        terms.add(term);
        JSONArray response = autocomplete.generateResponse(terms);
        JSONObject o = (JSONObject) response.get(0);
        assertEquals(term.getBasicForm(), o.get("basicForm"));
        assertEquals(term.getMeaning(), o.get("meaning"));
        assertEquals(String.valueOf(term.getLanguage()), o.get("language"));
        assertEquals(term.getId(), o.get("id"));
        assertEquals(term.getBasicForm() + " - " + StringUtils.abbreviate(term.getMeaning(), 32),
                o.get("value"));
    }

    @Test
    public void Generate_Response_One_Element_Found_And_Meaning_Is_Null() {
        List<Object> terms = new ArrayList<Object>();
        Term term = new Term();
        term.setBasicForm("kuusi");
        term.setLanguage(TermLanguage.FINNISH);
        terms.add(term);
        JSONArray response = autocomplete.generateResponse(terms);
        JSONObject o = (JSONObject) response.get(0);
        assertEquals(term.getBasicForm(), o.get("basicForm"));
        assertEquals(String.valueOf(term.getLanguage()), o.get("language"));
        assertEquals(term.getBasicForm(), o.get("value"));
    }
}
