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
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.UserTO;
import com.mysema.edith.services.UserDao;

@Transactional
@Path("/users")
@Produces(MediaType.APPLICATION_JSON)
public class UsersResource extends AbstractResource<UserTO> {

    private final UserDao dao;

    @Inject
    public UsersResource(UserDao dao) {
        this.dao = dao;
    }

    @GET @Path("current")
    public UserTO getCurrent() {
        return convert(dao.getCurrentUser(), new UserTO());
    }
    
    @Override
    @GET @Path("{id}")
    public UserTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new UserTO());
    }

    @Override
    @POST
    public UserTO create(UserTO info) {
        return convert(dao.save(convert(info, new User())), new UserTO());
    }

    @Override
    @PUT @Path("{id}")
    public UserTO update(@PathParam("id") Long id, UserTO info) {
        User entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(dao.save(convert(info, entity)), new UserTO());
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        throw new UnsupportedOperationException();
    }

}
