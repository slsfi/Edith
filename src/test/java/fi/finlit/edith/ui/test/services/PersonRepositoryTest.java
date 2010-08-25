package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.HashSet;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.domain.NameForm;
import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.PersonRepository;

public class PersonRepositoryTest extends AbstractServiceTest {
    @Inject
    private PersonRepository personRepository;

    private Person person;

    @Before
    public void Before() {
        person = new Person(new NameForm(), new HashSet<NameForm>());
        person.getNormalizedForm().setFirst("Simon");
        person.getNormalizedForm().setLast("Garfunkel");
        personRepository.save(person);
    }

    @After
    public void After() {
        personRepository.remove(person);
    }

    @Test
    public void Find_By_Start_Of_First_Name() {
        assertTrue(personRepository.findByStartOfFirstAndLastName("d", 10).isEmpty());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("Si", 10).size());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("si", 10).size());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("SI", 10).size());
    }

    @Test
    public void Find_By_Start_Of_Last_Name() {
        assertTrue(personRepository.findByStartOfFirstAndLastName("e", 10).isEmpty());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("Ga", 10).size());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("ga", 10).size());
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("GA", 10).size());
    }

    // FIXME
    @Test
    @Ignore
    public void Find_By_First_Name_And_Start_Of_Last_Name() {
        assertEquals(1, personRepository.findByStartOfFirstAndLastName("Simon Gar", 10).size());
    }
}
