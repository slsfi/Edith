package fi.finlit.edith.ui.services;

import java.util.Collection;

import org.springframework.transaction.annotation.Transactional;

import fi.finlit.edith.domain.Person;

@Transactional
public interface PersonRepository extends Repository<Person, String> {

    Collection<Person> findByStartOfFirstAndLastName(String partial, int limit);

    void remove(String personId);

    void remove(Person person);

    void save(Person person);

}
