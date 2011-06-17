package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.sql.domain.NameForm;
import fi.finlit.edith.sql.domain.Person;
import fi.finlit.edith.ui.services.PersonDao;


public class PersonDaoTest extends AbstractHibernateTest {
    @Inject
    private PersonDao personDao;

    private Person person;

    @Before
    public void Before() {
        person = new Person(new NameForm());
        person.getNormalizedForm().setFirst("Simon");
        person.getNormalizedForm().setLast("Garfunkel");
        personDao.save(person);
    }

    @Test
    public void Remove(){
        assertNotNull(personDao.getById(person.getId()));
        personDao.remove(person.getId());
        assertNull(personDao.getById(person.getId()));
    }

    @Test
    public void Find_By_Start_Of_First_Name() {
        assertTrue(personDao.findByStartOfFirstAndLastName("d", 10).isEmpty());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("Si", 10).size());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("si", 10).size());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("SI", 10).size());
    }

    @Test
    public void Find_By_Start_Of_Last_Name() {
        assertTrue(personDao.findByStartOfFirstAndLastName("e", 10).isEmpty());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("Ga", 10).size());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("ga", 10).size());
        assertEquals(1, personDao.findByStartOfFirstAndLastName("GA", 10).size());
    }

    @Test
    @Ignore
    public void Find_By_First_Name_And_Start_Of_Last_Name() {
        assertEquals(1, personDao.findByStartOfFirstAndLastName("Simon Gar", 10).size());
    }
}
