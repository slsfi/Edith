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
import com.mysema.edith.domain.Term;
import com.mysema.edith.dto.TermTO;
import com.mysema.edith.services.TermDao;

@Transactional
@Path("/terms")
@Produces(MediaType.APPLICATION_JSON)
public class TermsResource extends AbstractResource<TermTO> {

    private final TermDao dao;

    @Inject
    public TermsResource(TermDao dao) {
        this.dao = dao;
    }

    @Override
    @GET @Path("{id}")
    public TermTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new TermTO());
    }

    @Override
    @POST
    public TermTO create(TermTO info) {
        Term entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @Override
    @PUT @Path("{id}")
    public TermTO update(@PathParam("id") Long id, TermTO info) {
        dao.save(convert(info, new Term()));
        return info;
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
