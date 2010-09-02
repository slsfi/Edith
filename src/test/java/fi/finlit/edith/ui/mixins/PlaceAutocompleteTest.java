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
import fi.finlit.edith.domain.Place;

public class PlaceAutocompleteTest {
    private PlaceAutocomplete autocomplete;

    @Before
    public void Before() {
        autocomplete = new PlaceAutocomplete();
    }

    @Test
    public void Generate_Response() {
        List<Object> places = new ArrayList<Object>();
        places.add(new Place(new NameForm("Helsinki", "Capital of Finland."),
                new HashSet<NameForm>()));
        JSONArray response = autocomplete.generateResponse(places);
        assertEquals("Helsinki", ((JSONObject) response.get(0)).get("value"));
    }

}
