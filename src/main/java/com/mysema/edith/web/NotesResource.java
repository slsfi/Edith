package com.mysema.edith.web;

import java.util.ArrayList;
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
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.query.SearchResults;

@Transactional
@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource extends AbstractResource<NoteTO> {

    private final NoteDao dao;

    private final DocumentNoteDao documentNoteDao;

    @Inject
    public NotesResource(NoteDao dao, DocumentNoteDao documentNoteDao) {
        this.dao = dao;
        this.documentNoteDao = documentNoteDao;
    }

    @Override
    @GET @Path("{id}")
    public NoteTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new NoteTO());
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
        search.setLemma(query);
        search.setPage(page);
        search.setPerPage(perPage);
        search.setOrderBy(order);
        search.setAscending(direction.equals("ASC"));
        
        SearchResults<Note> results = dao.findNotes(search);        
        List<NoteTO> entries = new ArrayList<NoteTO>();
        for (Note note : results.getResults()) {
            entries.add(convert(note, new NoteTO()));
        }
        
        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", page);
        rv.put("perPage", perPage);
        rv.put("totalPages", totalPages(results.getLimit(), results.getTotal()));
        rv.put("totalEntries", results.getTotal());
        return rv;
    }

    private static final long totalPages(long pageSize, long count) {
        if (count == 0) {
            return 1;
        } else if (count % pageSize != 0) {
            return count / pageSize + 1;
        }
        return count / pageSize;
    }
    
    @POST @Path("query")
    public Map<String, Object> query(NoteSearchTO search) {
        SearchResults<Note> results = dao.findNotes(search);
        
        List<NoteTO> entries = new ArrayList<NoteTO>();
        for (Note note : results.getResults()) {
            entries.add(convert(note, new NoteTO()));
        }
        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", search.getPage());
        rv.put("perPage", search.getPage());
        rv.put("totalPages", totalPages(results.getLimit(), results.getTotal()));
        rv.put("totalEntries", results.getTotal());
        return rv;
    }

    @GET @Path("{id}/document-notes")
    public List<DocumentNoteTO> getDocumentNotes(@PathParam("id") Long id) {
        List<DocumentNote> docNotes = documentNoteDao.getOfNote(id);
        List<DocumentNoteTO> result = new ArrayList<DocumentNoteTO>(docNotes.size());
        for (DocumentNote docNote : docNotes) {
            result.add(convert(docNote, new DocumentNoteTO()));
        }
        return result;
    }

    @Override
    @POST
    public NoteTO create(NoteTO info) {
        return convert(dao.save(convert(info, new Note())), new NoteTO());
    }

    @Override
    @PUT @Path("{id}")
    public NoteTO update(@PathParam("id") Long id, NoteTO info) {
        Note entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(dao.save(convert(info, entity)), new NoteTO());
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
