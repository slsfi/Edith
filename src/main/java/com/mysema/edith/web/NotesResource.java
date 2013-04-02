package com.mysema.edith.web;

import static com.mysema.query.support.Expressions.stringPath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
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
import com.google.inject.Provider;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.QNote;
import com.mysema.edith.domain.QTerm;
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.NoteTO;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.query.jpa.HQLTemplates;
import com.mysema.query.jpa.impl.JPAQuery;
import com.mysema.query.support.Expressions;
import com.mysema.query.types.path.StringPath;

@Transactional
@Path("/notes")
@Produces(MediaType.APPLICATION_JSON)
public class NotesResource extends AbstractResource<NoteTO> {

    private final NoteDao dao;

    private final DocumentNoteDao documentNoteDao;

    @Inject
    private Provider<EntityManager> em;

    private JPAQuery query() {
        return new JPAQuery(em.get(), HQLTemplates.DEFAULT);
    }

    private static final QNote note = QNote.note;

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
            @QueryParam("page") long page,
            @QueryParam("per_page") long perPage,
            @QueryParam("order") String order,
            @QueryParam("direction") String direction) {
        QTerm term = QTerm.term;
        StringPath path = null;
        if (order == null) {
            path = note.lemma;
        } else if (order.startsWith("term.")) {
            path = stringPath(term, order.substring(1 + order.indexOf('.')));
        } else {
            path = stringPath(note, order);
        }
        List<Note> notes = query()
                .from(note)
                .leftJoin(note.term, term)
                .orderBy(direction == null || direction.equals("ASC") ? path.asc() : path.desc())
                .list(note);
        List<NoteTO> entries = new ArrayList<NoteTO>();
        for (Note note : notes) {
            entries.add(convert(note, new NoteTO()));
        }
        Map<String, Object> rv = new HashMap<String, Object>();
        rv.put("entries", entries);
        rv.put("currentPage", page);
        rv.put("perPage", perPage);
        rv.put("totalPages", 100);
        rv.put("totalEntries", 1000);
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
    public NoteTO update(NoteTO info) {
        Note entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @Override
    @PUT
    public NoteTO add(NoteTO info) {
        dao.save(convert(info, new Note()));
        return info;
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

}
