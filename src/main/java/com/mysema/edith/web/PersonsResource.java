/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Person;
import com.mysema.edith.dto.PersonTO;
import com.mysema.edith.services.PersonDao;

@Transactional
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonsResource extends AbstractResource {

    private final PersonDao personDao;

    @Inject
    public PersonsResource(PersonDao personDao) {
        this.personDao = personDao;
    }

    @GET @Path("{id}")
    public Object getById(@PathParam("id") Long id) {
        Person entity = personDao.getById(id);
        if (entity != null) {
            return convert(entity, PersonTO.class);
        }
        return NOT_FOUND;
    }

    @POST
    public PersonTO create(PersonTO info) {
        return convert(personDao.save(convert(info, Person.class)), PersonTO.class);
    }

    @PUT @Path("{id}")
    public PersonTO update(@PathParam("id") Long id,  Map<String, Object> info) {
        Person entity = personDao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(personDao.save(convert(info, entity)), PersonTO.class);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        personDao.remove(id);
    }

}
