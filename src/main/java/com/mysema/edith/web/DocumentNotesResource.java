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
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.services.DocumentNoteDao;

@Transactional
@Path("/documentnotes")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentNotesResource extends AbstractResource<DocumentNoteTO>{

    private final DocumentNoteDao dao;
    
    @Inject
    public DocumentNotesResource(DocumentNoteDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")
    public DocumentNoteTO getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new DocumentNoteTO());        
    }

    @POST
    public DocumentNoteTO update(DocumentNoteTO info) {
        DocumentNote entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT
    public DocumentNoteTO add(DocumentNoteTO info) {
        dao.save(convert(info, new DocumentNote()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }
    
}
