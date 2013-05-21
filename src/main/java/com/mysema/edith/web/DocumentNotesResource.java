/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.HashMap;
import java.util.Map;

import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.services.DocumentNoteService;

@Transactional
@Path("/document-notes")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentNotesResource extends AbstractResource {

    private final DocumentNoteService service;

    @Inject
    public DocumentNotesResource(DocumentNoteService service) {
        this.service = service;
    }

    @GET @Path("{id}")
    public Map<String, Object> getById(@PathParam("id") Long id,
                                  @QueryParam("note") boolean note) {
        Map<String, Object> rv = new HashMap<String, Object>();
        DocumentNote documentNote = service.getById(id);
        rv.put("documentNote", convert(documentNote, DocumentNoteTO.class));
        if (note) {
            rv.put("note", convert(documentNote.getNote(), NoteTO.class));
        }
        return rv;
    }

    @POST
    public DocumentNoteTO create(DocumentNoteTO info) {
        return convert(service.save(convert(info, new DocumentNote())), DocumentNoteTO.class);
    }

    @PUT @Path("{id}")
    public DocumentNoteTO update(@PathParam("id") Long id, DocumentNoteTO info) {
        DocumentNote entity = service.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(service.save(convert(info, entity)), DocumentNoteTO.class);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        service.remove(id);
    }

}
