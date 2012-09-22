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
import com.mysema.edith.domain.Document;
import com.mysema.edith.dto.DocumentInfo;
import com.mysema.edith.services.DocumentDao;

@Transactional
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentService extends AbstractService<DocumentInfo>{

    private final DocumentDao dao;
    
    @Inject
    public DocumentService(DocumentDao dao) {
        this.dao = dao;
    }
    
    @GET @Path("{id}")    
    public DocumentInfo getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new DocumentInfo());        
    }

    @POST
    public DocumentInfo update(DocumentInfo info) {
        Document entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @PUT 
    public DocumentInfo add(DocumentInfo info) {
        dao.save(convert(info, new Document()));
        return info;
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }
    
    // TODO addDocumentsFromZip
    
    // TODO document rendering

}
