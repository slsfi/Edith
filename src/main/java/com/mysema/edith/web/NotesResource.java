/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

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
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.DocumentNoteInfo;
import com.mysema.edith.dto.NoteInfo;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;

@Transactional
@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource extends AbstractResource<NoteInfo> {

    private final NoteDao dao;
    
    private final DocumentNoteDao documentNoteDao;
    
    @Inject
    public NotesResource(NoteDao dao, DocumentNoteDao documentNoteDao) {
        this.dao = dao;
        this.documentNoteDao = documentNoteDao;
    }
    
    @GET @Path("{id}")
    public NoteInfo getById(@PathParam("id") Long id) {        
        return convert(dao.getById(id), new NoteInfo());        
    }
    
    @GET @Path("{id}/document-notes")
    public List<DocumentNoteInfo> getDocumentNotes(@PathParam("id") Long id) {
        List<DocumentNote> docNotes = documentNoteDao.getOfNote(id);
        List<DocumentNoteInfo> result = new ArrayList<DocumentNoteInfo>(docNotes.size());
        for (DocumentNote docNote : docNotes) {
            result.add(convert(docNote, new DocumentNoteInfo()));
        }
        return result;
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
