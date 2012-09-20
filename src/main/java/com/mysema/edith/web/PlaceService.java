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
import com.mysema.edith.dto.PlaceInfo;
import com.mysema.edith.services.PlaceDao;

@Transactional
@Path("/places")
@Produces(MediaType.APPLICATION_JSON)
public class PlaceService extends AbstractService<PlaceInfo> {

    private final PlaceDao dao;
    
    @Inject
    public PlaceService(PlaceDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")
    public PlaceInfo getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new PlaceInfo());        
    }

    @POST
    public PlaceInfo update(PlaceInfo info) {
        Place entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT 
    public PlaceInfo add(PlaceInfo info) {
        dao.save(convert(info, new Place()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
