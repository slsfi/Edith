/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import java.util.List;
import java.util.Map;

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
public class UsersResource extends AbstractResource {

    private final UserDao userDao;

    @Inject
    public UsersResource(UserDao userDao) {
        this.userDao = userDao;
    }

    @GET
    public List<UserTO> all() {
        return convert(userDao.getAll(), UserTO.class);
    }

    @GET @Path("current")
    public UserTO getCurrent() {
        return convert(userDao.getCurrentUser(), UserTO.class);
    }

    @GET @Path("{id}")
    public Object getById(@PathParam("id") Long id) {
        User entity = userDao.getById(id);
        if (entity != null) {
            return convert(entity, UserTO.class);
        }
        return NOT_FOUND;
    }

    @POST
    public UserTO create(UserTO info) {
        return convert(userDao.save(convert(info, User.class)), UserTO.class);
    }

    @PUT @Path("{id}")
    public UserTO update(@PathParam("id") Long id,  Map<String, Object> info) {
        User entity = userDao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(userDao.save(convert(info, entity)), UserTO.class);
    }
}
