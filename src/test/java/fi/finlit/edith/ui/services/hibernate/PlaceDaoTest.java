package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.sql.domain.NameForm;
import fi.finlit.edith.sql.domain.Place;
import fi.finlit.edith.ui.services.PlaceDao;

public class PlaceDaoTest extends AbstractHibernateTest {
    
    @Inject
    private PlaceDao placeDao;

    private Place place;

    @Before
    public void Before() {
        place = new Place(new NameForm(), new HashSet<NameForm>());
        place.getNormalizedForm().setName("Stockholm");
        placeDao.save(place);
    }

    @Test
    public void Remove() {
        assertNotNull(placeDao.getById(place.getId()));
        placeDao.remove(place.getId());
        assertNull(placeDao.getById(place.getId()));
    }

    @Test
    public void Find_By_Start_Of_Name() {
        assertTrue(placeDao.findByStartOfName("d", 10).isEmpty());
        assertEquals(1, placeDao.findByStartOfName("St", 10).size());
        assertEquals(1, placeDao.findByStartOfName("st", 10).size());
        assertEquals(1, placeDao.findByStartOfName("ST", 10).size());
    }
}
