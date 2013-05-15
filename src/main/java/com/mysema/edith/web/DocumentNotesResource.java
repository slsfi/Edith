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
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.SelectionTO;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteService;
import com.mysema.edith.services.NoteDao;

@Transactional
@Path("/documentnotes")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentNotesResource extends AbstractResource<DocumentNoteTO>{

    private final NoteDao noteDao;

    private final DocumentDao documentDao;

    private final DocumentNoteService service;

    @Inject
    public DocumentNotesResource(NoteDao noteDao, DocumentDao documentDao,
            DocumentNoteService service) {
        this.noteDao = noteDao;
        this.documentDao = documentDao;
        this.service = service;
    }

    @Override
    @GET @Path("{id}")
    public DocumentNoteTO getById(@PathParam("id") Long id) {
        return convert(service.getById(id), new DocumentNoteTO());
    }

    @Override
    @POST
    public DocumentNoteTO create(DocumentNoteTO info) {
        return convert(service.save(convert(info, new DocumentNote())), new DocumentNoteTO());
    }

    @Override
    @PUT @Path("{id}")
    public DocumentNoteTO update(@PathParam("id") Long id, DocumentNoteTO info) {
        DocumentNote entity = service.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(service.save(convert(info, entity)), new DocumentNoteTO());
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        service.remove(id);
    }

}
