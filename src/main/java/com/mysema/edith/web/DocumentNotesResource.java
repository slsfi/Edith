/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.HashMap;
import java.util.List;
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
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.FullDocumentNoteTO;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.services.DocumentNoteService;
import com.mysema.edith.services.NoteDao;
import com.mysema.query.SearchResults;

@Transactional
@Path("/document-notes")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentNotesResource extends AbstractResource {

    private final DocumentNoteService service;

    private final NoteDao dao;

    @Inject
    public DocumentNotesResource(DocumentNoteService service, NoteDao dao) {
        this.service = service;
        this.dao = dao;
    }

    @GET @Path("{id}")
    public FullDocumentNoteTO getById(@PathParam("id") Long id) {
        return convert(service.getById(id), FullDocumentNoteTO.class);
    }

    @GET
    public Map<String, Object> all(
            @QueryParam("page") Long page,
            @QueryParam("per_page") Long perPage,
            @QueryParam("order") String order,
            @QueryParam("direction") String direction,
            @QueryParam("query") String query) {

        if (perPage == null) {
            perPage = 25L;
        } else if (perPage <= 0) {
            perPage = (long) Integer.MAX_VALUE;
        }
        if (page == null) {
            page = 1L;
        }

        NoteSearchTO search = new NoteSearchTO();
        search.setQuery(query);
        search.setPage(page);
        search.setPerPage(perPage);
        search.setOrder(order);
        search.setAscending(direction == null || direction.equals("ASC"));

        SearchResults<DocumentNote> results = dao.findDocumentNotes(search);
        List<FullDocumentNoteTO> entries = convert(results.getResults(), FullDocumentNoteTO.class);

        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", page);
        rv.put("perPage", perPage);
        rv.put("totalPages", totalPages(results.getLimit(), results.getTotal()));
        rv.put("totalEntries", results.getTotal());
        return rv;
    }

    @POST @Path("query")
    public Map<String, Object> query(NoteSearchTO search) {
        SearchResults<DocumentNote> results = dao.findDocumentNotes(search);
        List<FullDocumentNoteTO> entries = convert(results.getResults(), FullDocumentNoteTO.class);

        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", search.getPage());
        rv.put("perPage", search.getPage());
        rv.put("totalPages", totalPages(results.getLimit(), results.getTotal()));
        rv.put("totalEntries", results.getTotal());
        return rv;
    }

    private Note getNote(Map<String, Object> info) {
        Note note;
        if (info.containsKey("id")) {
            note = dao.getById(Long.parseLong(info.get("id").toString()));
        } else {
            note = new Note();
        }
        info.remove("lastEditedBy");
        info.remove("allEditors");
        return dao.save(convert(info, note));
    }

    @POST
    public FullDocumentNoteTO create(Map<String, Object> info) {
        Object selection = info.remove("selection");
        DocumentNote documentNote;
        if (info.get("note") instanceof Map) {
            info.put("note", getNote((Map<String, Object>) info.get("note")));
        }
        if (selection != null) {
            SelectedText text = convert(selection, SelectedText.class);
            Document doc = convert(info.get("document"), Document.class);
            if (info.containsKey("note")) {
                Note note = convert(info.get("note"), Note.class);
                documentNote = service.attachNote(note, doc, text);
            } else {
                documentNote = service.attachNote(doc, text);
            }
        } else {
            documentNote = new DocumentNote();
        }
        return convert(service.save(convert(info, documentNote)), FullDocumentNoteTO.class);
    }

    @PUT @Path("{id}")
    public FullDocumentNoteTO update(@PathParam("id") Long id, Map<String, Object> info) {
        DocumentNote entity = service.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        if (info.get("note") instanceof Map) {
            info.put("note", getNote((Map<String, Object>) info.get("note")));
        }
        Object selection = info.remove("selection");
        if (selection != null) {
            SelectedText text = convert(selection, SelectedText.class);
            entity = service.updateNote(entity, text);
        }
        return convert(service.save(convert(info, entity)), FullDocumentNoteTO.class);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        service.remove(id);
    }

}
