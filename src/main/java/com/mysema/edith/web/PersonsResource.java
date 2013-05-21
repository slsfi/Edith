/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

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
public class PersonsResource extends AbstractResource<PersonTO> {

    private final PersonDao dao;

    @Inject
    public PersonsResource(PersonDao dao) {
        this.dao = dao;
    }

    @GET @Path("{id}")
    public PersonTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new PersonTO());
    }

    @POST
    public PersonTO create(PersonTO info) {
        return convert(dao.save(convert(info, new Person())), new PersonTO());
    }

    @PUT @Path("{id}")
    public PersonTO update(@PathParam("id") Long id, PersonTO info) {
        Person entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(dao.save(convert(info, entity)), new PersonTO());
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
