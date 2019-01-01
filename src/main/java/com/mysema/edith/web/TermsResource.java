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
import com.mysema.edith.domain.Term;
import com.mysema.edith.dto.TermTO;
import com.mysema.edith.services.TermDao;

@Transactional
@Path("/terms")
@Produces(MediaType.APPLICATION_JSON)
public class TermsResource extends AbstractResource {

    private final TermDao termDao;

    @Inject
    public TermsResource(TermDao termDao) {
        this.termDao = termDao;
    }

    @GET @Path("{id}")
    public Object getById(@PathParam("id") Long id) {
        Term entity = termDao.getById(id);
        if (entity != null) {
            return convert(entity, TermTO.class);
        }
        return NOT_FOUND;
    }

    @POST
    public TermTO create(TermTO info) {
        return convert(termDao.save(convert(info, Term.class)), TermTO.class);
    }

    @PUT @Path("{id}")
    public TermTO update(@PathParam("id") Long id,  Map<String, Object> info) {
        Term entity = termDao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(termDao.save(convert(info, entity)), TermTO.class);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        termDao.remove(id);
    }

}
