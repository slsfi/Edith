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

import com.google.inject.Inject;
import com.mysema.edith.domain.User;
import com.mysema.edith.dto.UserTO;
import com.mysema.edith.services.UserDao;

public class UsersResource extends AbstractResource<UserTO> {

    private final UserDao dao;
    
    @Inject
    public UsersResource(UserDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")
    public UserTO getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new UserTO());        
    }

    @POST
    public UserTO update(UserTO info) {
        User entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT 
    public UserTO add(UserTO info) {
        dao.save(convert(info, new User()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        throw new UnsupportedOperationException();
    }

}
