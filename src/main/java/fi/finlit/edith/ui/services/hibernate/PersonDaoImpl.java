package fi.finlit.edith.ui.services.hibernate;

import static fi.finlit.edith.sql.domain.QPerson.person;

import java.util.Collection;

import fi.finlit.edith.sql.domain.Person;
import fi.finlit.edith.ui.services.PersonDao;

public class PersonDaoImpl extends AbstractDao<Person> implements PersonDao {
    @Override
    public Collection<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return query()
                .from(person)
                .where(person.normalizedForm.first.startsWithIgnoreCase(partial).or(
                        person.normalizedForm.last.startsWithIgnoreCase(partial))).limit(limit)
                .list(person);
    }

    @Override
    public void remove(Long personId) {
        // FIXME: Hibernatify!
        Person entity = getById(personId);
        if (entity != null) {
            getSession().delete(entity);
        }
    }

    @Override
    public void save(Person person) {
        getSession().save(person);
    }

    @Override
    public void remove(Person person) {
        getSession().delete(person);
    }

    @Override
    public Collection<Person> getAll() {
        return query().from(person).list(person);
    }

    @Override
    public Person getById(Long id) {
        return query().from(person).where(person.id.eq(id)).uniqueResult(person);
    }

}