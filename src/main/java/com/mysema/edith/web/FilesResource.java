package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.dto.DocumentNoteInfo;
import com.mysema.edith.dto.FileItemWithDocumentId;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteDao;

@Transactional
@Path("/files")
@Produces(MediaType.APPLICATION_JSON)
public class FilesResource extends AbstractResource<FileItemWithDocumentId>{

    private final DocumentDao dao;

    @Inject
    public FilesResource(DocumentDao dao) {
        this.dao = dao;
    }

    @Override
    @GET @Path("{id}")
    public FileItemWithDocumentId getById(@PathParam("id") Long id) {
        throw new UnsupportedOperationException();
    }

    @GET
    public List<FileItemWithDocumentId> getFiles(
            @QueryParam("id") Long id,
            @QueryParam("path") String path) {
        return dao.fromPath(path, id);
    }

    @Override
    public FileItemWithDocumentId update(FileItemWithDocumentId file) {
        throw new UnsupportedOperationException();
    }

    @Override
    @PUT
    public FileItemWithDocumentId add(FileItemWithDocumentId file) {
        throw new UnsupportedOperationException();
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        throw new UnsupportedOperationException();
    }
}
