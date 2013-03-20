/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.DELETE;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.xml.stream.XMLOutputFactory;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.google.inject.persist.Transactional;
import com.mysema.edith.EDITH;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteComment;
import com.mysema.edith.dto.DocumentInfo;
import com.mysema.edith.dto.DocumentNoteInfo;
import com.mysema.edith.dto.NoteCommentTO;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.services.ContentRenderer;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;

@Transactional
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentsResource extends AbstractResource<DocumentInfo>{

    private final DocumentDao dao;

    private final DocumentNoteDao documentNoteDao;

    // TODO: Remove once not needed
    private final NoteDao noteDao;

    private final ContentRenderer renderer;

    private static final XMLOutputFactory factory = XMLOutputFactory.newInstance();

    // TODO: Remove once not needed
    @Inject @Named(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject
    public DocumentsResource(
            DocumentDao dao,
            DocumentNoteDao documentNoteDao,
            NoteDao noteDao,
            ContentRenderer renderer) {
        this.dao = dao;
        this.documentNoteDao = documentNoteDao;
        this.noteDao = noteDao;
        this.renderer = renderer;
    }

    @Override
    @GET @Path("{id}")
    public DocumentInfo getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new DocumentInfo());
    }

    @GET @Path("{id}/document-notes")
    public List<DocumentNoteInfo> getDocumentNotes(@PathParam("id") Long id) {
        List<DocumentNote> docNotes = documentNoteDao.getOfDocument(id);
        List<DocumentNoteInfo> result = new ArrayList<DocumentNoteInfo>(docNotes.size());
        for (DocumentNote docNote : docNotes) {
            result.add(convert(docNote, new DocumentNoteInfo()));
        }
        return result;
    }

    @Override
    @POST
    public DocumentInfo update(DocumentInfo info) {
        Document entity = dao.getById(info.getId());
        if (entity != null) {
            dao.save(convert(info, entity));
        }
        return info;
    }

    @Override
    @PUT
    public DocumentInfo add(DocumentInfo info) {
        dao.save(convert(info, new Document()));
        return info;
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }

    // TODO addDocumentsFromZip

    @GET
    @Path("{id}/raw")
    public void getRawDocument(
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws Exception {
    	response.setContentType("text/html; charset=utf-8");
        renderer.renderDocument(dao.getById(id), factory.createXMLStreamWriter(response.getWriter()));
    }

    @GET
    @Path("{id}/notecomments")
    public List<NoteCommentTO> getLatestComments(@PathParam("id") Long id) {
        List<NoteComment> noteComments = dao.getNoteComments(id, 3);
        // TODO: Remove once there is an UI implementation for adding comments
        if (noteComments.isEmpty()) {
            Document document = dao.getDocumentForPath(documentRoot + "/Nummisuutarit rakenteistettuna.xml");
            String element = "play-act-sp2-p";
            String text = "sun ullakosta ottaa";

            try {
                DocumentNote docNote = dao.addNote(new Note(), document,
                        new SelectedText(element, element, text));
                noteDao.createComment(docNote.getNote(), "Yay");
                noteDao.createComment(docNote.getNote(), "Hay");
                noteDao.createComment(docNote.getNote(), "Bay");
            } catch (Exception e) {
                System.err.println("nooooooooooo");
            }
            noteComments = dao.getNoteComments(document.getId(), 3);
        }
        List<NoteCommentTO> result = new ArrayList<NoteCommentTO>();
        for (NoteComment noteComment : noteComments) {
            result.add(convert(noteComment, new NoteCommentTO()));
        }
        return result;
    }
}
