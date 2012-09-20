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
import com.mysema.edith.dto.PersonInfo;
import com.mysema.edith.services.PersonDao;

@Transactional
@Path("/persons")
@Produces(MediaType.APPLICATION_JSON)
public class PersonService extends AbstractService<PersonInfo> {

    private final PersonDao dao;
    
    @Inject
    public PersonService(PersonDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")
    public PersonInfo getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new PersonInfo());        
    }

    @POST
    public PersonInfo update(PersonInfo info) {
        Person entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT 
    public PersonInfo add(PersonInfo info) {
        dao.save(convert(info, new Person()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
