/*
 * Copyright (c) 2010 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static fi.finlit.edith.domain.QPerson.person;

import java.util.Collection;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.dao.AbstractRepository;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.Person;
import fi.finlit.edith.domain.PersonRepository;

public class PersonRepositoryImpl extends AbstractRepository<Person> implements PersonRepository {
    public PersonRepositoryImpl(@Inject SessionFactory sessionFactory) {
        super(sessionFactory, person);
    }

    @Override
    public Collection<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return getSession()
                .from(person)
                .where(person.normalizedForm().first.startsWith(partial, false).or(
                        person.normalizedForm().last.startsWith(partial, false))).limit(limit)
                .list(person);
    }
}