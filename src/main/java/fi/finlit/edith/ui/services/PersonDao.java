package fi.finlit.edith.ui.services;

import java.util.Collection;

import fi.finlit.edith.sql.domain.Person;

public interface PersonDao extends Dao<Person, Long> {

    Collection<Person> findByStartOfFirstAndLastName(String partial, int limit);

    void remove(Long personId);

    void remove(Person person);

    void save(Person person);

}
