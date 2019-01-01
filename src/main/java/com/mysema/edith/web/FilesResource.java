/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.web;

import java.util.Collections;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Document;
import com.mysema.edith.dto.FileItem;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.services.DocumentDao;

@Transactional
@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesResource {

    private final DocumentDao documentDao;

    @Inject
    public FilesResource(DocumentDao documentDao) {
        this.documentDao = documentDao;
    }

    @GET
    public List<FileItemWithDocumentId> getFiles(
            @QueryParam("id") Long id,
            @QueryParam("path") String path) {
        return documentDao.fromPath(path, id);
    }
    
    @DELETE
    public void delete(@FormParam("path") String path) {
        documentDao.removeByPath(path);
    }
    
    @PUT 
    public FileItem move(@FormParam("path") String path, @FormParam("name") String name) {
        Document doc = documentDao.getDocumentForPath(path);
        if (doc == null) {
            throw new RuntimeException("Document not found " + path);
        }
        doc = documentDao.rename(doc.getId(), name);
        return new FileItem(doc.getTitle(), doc.getPath(), false, Collections.<FileItem>emptyList(), false);
    }

}
