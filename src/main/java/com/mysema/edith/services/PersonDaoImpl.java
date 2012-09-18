package com.mysema.edith.services;

import java.util.Collection;

import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Person;

@Transactional
public class PersonDaoImpl extends AbstractDao<Person> implements PersonDao {

    @Override
    public Collection<Person> findByStartOfFirstAndLastName(String partial, int limit) {
        return query()
                .from(person)
                .where(person.normalized.first.startsWithIgnoreCase(partial).or(
                        person.normalized.last.startsWithIgnoreCase(partial))).limit(limit)
                .list(person);
    }

    @Override
    public void remove(Long personId) {
        // FIXME: Hibernatify!
        Person entity = getById(personId);
        if (entity != null) {
            getEntityManager().remove(entity);
        }
    }

    @Override
    public void save(Person person) {
        getEntityManager().persist(person);
    }

    @Override
    public Person getById(Long id) {
        return query().from(person).where(person.id.eq(id)).uniqueResult(person);
    }

}