/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QPerson.person;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Person;

public class PersonRepositoryImpl extends AbstractRepository<Person> implements PersonRepository {
    public PersonRepositoryImpl(@Inject SessionFactory sessionFactory) {
        super(sessionFactory, person);
    }

    @Override
    public Collection<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return getSession()
                .from(person)
                .where(person.normalizedForm().first.startsWithIgnoreCase(partial).or(
                        person.normalizedForm().last.startsWithIgnoreCase(partial))).limit(limit)
                .list(person);
    }

    @Override
    public void remove(String personId) {
        Person entity = getById(personId);
        if (entity != null){
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

}