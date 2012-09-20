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
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.NoteInfo;
import com.mysema.edith.services.NoteDao;

@Transactional
@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NoteService extends AbstractService<NoteInfo> {

    private final NoteDao dao;
    
    @Inject
    public NoteService(NoteDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")
    public NoteInfo getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new NoteInfo());        
    }

    @POST
    public NoteInfo update(NoteInfo info) {
        Note entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT 
    public NoteInfo add(NoteInfo info) {
        dao.save(convert(info, new Note()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
