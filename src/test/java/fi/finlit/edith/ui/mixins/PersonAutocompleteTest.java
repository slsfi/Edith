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
    public void Generate_Response() {
        List<Object> persons = new ArrayList<Object>();
        persons.add(new Person(new NameForm("Foo", "Bar", "The strangest name ever."),
                new HashSet<NameForm>()));
        JSONArray response = autocomplete.generateResponse(persons);
        assertEquals("Foo Bar", ((JSONObject) response.get(0)).get("value"));
    }

}
