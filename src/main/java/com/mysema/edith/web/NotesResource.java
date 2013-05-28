package com.mysema.edith.web;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ws.rs.Consumes;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import com.google.common.collect.Lists;
import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.NoteSearchTO;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.util.StringUtils;
import com.mysema.query.SearchResults;
import com.mysema.util.BeanMap;

import com.sun.jersey.multipart.FormDataParam;

@Transactional
@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource extends AbstractResource {

    private final NoteDao dao;

    private final DocumentNoteDao documentNoteDao;

    @Inject
    public NotesResource(NoteDao dao, DocumentNoteDao documentNoteDao) {
        this.dao = dao;
        this.documentNoteDao = documentNoteDao;
    }

    @GET @Path("{id}")
    public NoteTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), NoteTO.class);
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
        search.setOrder(order);
        search.setAscending(direction == null || direction.equals("ASC"));

        SearchResults<Note> results = dao.findNotes(search);
        List<NoteTO> entries = convert(results.getResults(), NoteTO.class);

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
        SearchResults<Note> results = dao.findNotes(search);
        List<NoteTO> entries = convert(results.getResults(), NoteTO.class);

        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", search.getPage());
        rv.put("perPage", search.getPage());
        rv.put("totalPages", totalPages(results.getLimit(), results.getTotal()));
        rv.put("totalEntries", results.getTotal());
        return rv;
    }

    @POST @Path("query")
    @Produces("text/csv")
    public Response queryCsv(NoteSearchTO search) {
        search.setPage(null);
        search.setPerPage(null);
        SearchResults<Note> results = dao.findNotes(search);

        StringBuilder builder = new StringBuilder();
        if (search.getColumns() != null && !search.getColumns().isEmpty()) {
            builder.append(StringUtils.join(search.getColumns(), ";"));
            builder.append("\n");
            for (Note note : results.getResults()) {
                BeanMap noteBean = new BeanMap(note);
                BeanMap termBean = note.getTerm() != null ? new BeanMap(note.getTerm()) : new BeanMap();
                List<String> values = Lists.newArrayList();
                for (String column : search.getColumns()) {
                    Object value = null;
                    if (column.startsWith("term")) {
                        value = termBean.get(column.substring(column.indexOf(".")+1));
                    } else {
                        value = noteBean.get(column);
                    }
                    values.add(value != null ? value.toString() : "");
                }
                builder.append(StringUtils.join(values, ";"));
                builder.append("\n");
            }
        }

        return Response.ok(builder.toString(), "text/csv")
                .header("Content-Disposition", "attachment; filename=results.csv")
                .build();
    }

    @GET @Path("{id}/document-notes")
    public List<DocumentNoteTO> getDocumentNotes(@PathParam("id") Long id) {
        List<DocumentNote> docNotes = documentNoteDao.getOfNote(id);
        return convert(docNotes, DocumentNoteTO.class);
    }

    @POST
    public NoteTO create(NoteTO info) {
        return convert(dao.save(convert(info, new Note())), NoteTO.class);
    }

    @PUT @Path("{id}")
    public NoteTO update(@PathParam("id") Long id,  Map<String, Object> info) {
        Note entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        info.remove("lastEditedBy");
        info.remove("allEditors");
        return convert(dao.save(convert(info, entity)), NoteTO.class);
    }

    @POST
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public int importNotes(@FormDataParam("file") InputStream stream) {
        return dao.importNotes(stream);
    }

    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
