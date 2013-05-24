/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.services;

import java.util.List;

import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Person;
import com.mysema.edith.domain.QPerson;

@Transactional
public class PersonDaoImpl extends AbstractDao<Person> implements PersonDao {

    private static final QPerson person = QPerson.person;

    @Override
    public List<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return from(person)
               .where(
                   person.normalized.first.startsWithIgnoreCase(partial).or(
                   person.normalized.last.startsWithIgnoreCase(partial)))
               .limit(limit)
               .list(person);
    }

    @Override
    public void remove(Long personId) {
        Person entity = getById(personId);
        if (entity != null) {
            remove(entity);
        }
    }

    @Override
    public Person save(Person person) {
        return persistOrMerge(person);
    }

    @Override
    public Person getById(Long id) {
        return find(Person.class, id);
    }

}