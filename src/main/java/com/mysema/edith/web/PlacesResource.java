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
import com.mysema.edith.domain.Place;
import com.mysema.edith.dto.PlaceTO;
import com.mysema.edith.services.PlaceDao;

@Transactional
@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public class PlacesResource extends AbstractResource {

    private final PlaceDao placeDao;

    @Inject
    public PlacesResource(PlaceDao placeDao) {
        this.placeDao = placeDao;
    }

    @GET @Path("{id}")
    public Object getById(@PathParam("id") Long id) {
        Place entity = placeDao.getById(id);
        if (entity != null) {
            return convert(entity, PlaceTO.class);
        }
        return NOT_FOUND;
    }

    @POST
    public PlaceTO create(PlaceTO info) {
        return convert(placeDao.save(convert(info, new Place())), PlaceTO.class);
    }

    @PUT @Path("{id}")
    public PlaceTO update(@PathParam("id") Long id,  Map<String, Object> info) {
        Place entity = placeDao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(placeDao.save(convert(info, entity)), PlaceTO.class);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        placeDao.remove(id);
    }

}
