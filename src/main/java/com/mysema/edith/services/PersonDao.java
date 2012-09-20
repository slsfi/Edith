package com.mysema.edith.services;

import java.util.Collection;

import com.mysema.edith.domain.Person;

/**
 * @author tiwe
 *
 */
public interface PersonDao extends Dao<Person, Long> {

    /**
     * @param partial
     * @param limit
     * @return
     */
    Collection<Person> findByStartOfFirstAndLastName(String partial, int limit);

    /**
     * @param personId
     */
    void remove(Long personId);

    /**
     * @param person
     */
    void save(Person person);

}