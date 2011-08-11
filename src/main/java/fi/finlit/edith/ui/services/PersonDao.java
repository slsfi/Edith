package fi.finlit.edith.ui.services;

import java.util.Collection;

import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.sql.domain.Person;

public interface PersonDao extends Dao<Person, Long> {

    Collection<Person> findByStartOfFirstAndLastName(String partial, int limit);

    @CommitAfter
    void remove(Long personId);

    @CommitAfter
    void save(Person person);

}
