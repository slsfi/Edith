package fi.finlit.edith.ui.mixins;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.tapestry5.json.JSONArray;
import org.apache.tapestry5.json.JSONObject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;

public class PersonAutocompleteTest {
    private PersonAutocomplete autocomplete;

    @Before
    public void Before() {
        autocomplete = new PersonAutocomplete();
    }

    @Test
    public void Generate_Response_When_No_Description() {
        List<Object> persons = new ArrayList<Object>();
        persons.add(new Person(new NameForm("Foo", "Bar", null),
                new HashSet<NameForm>()));
        JSONArray response = autocomplete.generateResponse(persons);
        assertEquals("Foo Bar", ((JSONObject) response.get(0)).get("value"));
    }

    @Test
    public void Generate_Response_When_Short_Description() {
        List<Object> persons = new ArrayList<Object>();
        persons.add(new Person(new NameForm("Foo", "Bar", "I am short."),
                new HashSet<NameForm>()));
        JSONArray response = autocomplete.generateResponse(persons);
        assertEquals("Foo Bar - I am short.", ((JSONObject) response.get(0)).get("value"));
    }

    @Test
    public void Generate_Response_When_Long_Description() {
        List<Object> persons = new ArrayList<Object>();
        persons.add(new Person(new NameForm("Foo", "Bar", "I am not short, I am very very very very long."),
                new HashSet<NameForm>()));
        JSONArray response = autocomplete.generateResponse(persons);
        assertEquals("Foo Bar - I am not short, I am very ver...", ((JSONObject) response.get(0)).get("value"));
    }

}
