package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Place;
import fi.finlit.edith.domain.PlaceRepository;

public class PlaceRepositoryTest extends AbstractServiceTest {
    @Inject
    private PlaceRepository placeRepository;

    private Place place;

    @Before
    public void Before() {
        place = new Place(new NameForm(), new HashSet<NameForm>());
        place.getNormalizedForm().setName("Stockholm");
        placeRepository.save(place);
    }

    @After
    public void After() {
        placeRepository.remove(place);
    }

    @Test
    public void Find_By_Start_Of_Name() {
        assertTrue(placeRepository.findByStartOfName("d", 10).isEmpty());
        assertEquals(1, placeRepository.findByStartOfName("St", 10).size());
        assertEquals(1, placeRepository.findByStartOfName("st", 10).size());
        assertEquals(1, placeRepository.findByStartOfName("ST", 10).size());
    }
}
