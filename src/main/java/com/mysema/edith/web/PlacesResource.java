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
import com.mysema.edith.domain.Place;
import com.mysema.edith.dto.PlaceTO;
import com.mysema.edith.services.PlaceDao;

@Transactional
@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public class PlacesResource extends AbstractResource<PlaceTO> {

    private final PlaceDao dao;

    @Inject
    public PlacesResource(PlaceDao dao) {
        this.dao = dao;
    }

    @Override
    @GET @Path("{id}")
    public PlaceTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new PlaceTO());
    }

    @Override
    @POST
    public PlaceTO create(PlaceTO info) {
        return convert(dao.save(convert(info, new Place())), new PlaceTO());
    }

    @Override
    @PUT @Path("{id}")
    public PlaceTO update(@PathParam("id") Long id, PlaceTO info) {
        Place entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(dao.save(convert(info, entity)), new PlaceTO());
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
