package com.mysema.edith.web;

import static org.junit.Assert.assertNotNull;

import org.joda.time.DateTime;
import org.junit.Test;

import com.google.common.collect.Sets;
import com.google.inject.Inject;
import com.mysema.edith.domain.Interval;
import com.mysema.edith.domain.NameForm;
import com.mysema.edith.domain.Person;
import com.mysema.edith.dto.PersonInfo;
import com.mysema.edith.services.PersonDao;

public class PersonsResourceTest extends AbstractResourceTest {
    
    @Inject
    private PersonDao personDao;
    
    @Inject
    private PersonsResource persons;
    
    @Test
    public void GetById() {
        Person person = new Person();
        person.setNormalized(new NameForm("a", "b"));
        person.setOtherForms(Sets.newHashSet(new NameForm("c","d")));
        person.setTimeOfBirth(Interval.createDate(new DateTime()));
        person.setTimeOfDeath(Interval.createDate(new DateTime()));
        personDao.save(person);
        
        assertNotNull(persons.getById(person.getId()));
    }

    @Test
    public void Add() {
        PersonInfo person = new PersonInfo();
        person.setNormalized(new NameForm("a", "b"));
        person.setOtherForms(Sets.newHashSet(new NameForm("c","d")));
        person.setTimeOfBirth(Interval.createDate(new DateTime()));
        person.setTimeOfDeath(Interval.createDate(new DateTime()));
        persons.add(person);    
    }
}
