package fi.finlit.edith.ui.mixins;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.List;

import org.apache.tapestry5.json.JSONArray;
import org.junit.Test;

public class JQueryAutocompleteTest {
    private JQueryAutocomplete autocomplete;

    @Test
    public void GenerateResponse() {
        autocomplete = new JQueryAutocomplete();
        List<Object> matches = new ArrayList<Object>();
        matches.add("foobar");
        JSONArray result = autocomplete.generateResponse(matches);
        assertEquals("foobar", result.get(0));
    }
}
