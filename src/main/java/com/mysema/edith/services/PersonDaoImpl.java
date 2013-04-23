/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.util.Collection;

import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Person;
import com.mysema.edith.domain.QPerson;

@Transactional
public class PersonDaoImpl extends AbstractDao<Person> implements PersonDao {

    private static final QPerson person = QPerson.person;

    @Override
    public Collection<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return query()
                .from(person)
                .where(person.normalized.first.startsWithIgnoreCase(partial).or(
                       person.normalized.last.startsWithIgnoreCase(partial)))
                .limit(limit)
                .list(person);
    }

    @Override
    public void remove(Long personId) {
        // FIXME: Hibernatify!
        Person entity = getById(personId);
        if (entity != null) {
            remove(entity);
        }
    }

    @Override
    public Person save(Person person) {
        persist(person);
        return person;
    }

    @Override
    public Person getById(Long id) {
        return query().from(person).where(person.id.eq(id)).uniqueResult(person);
    }

}