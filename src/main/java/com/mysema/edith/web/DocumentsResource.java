/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.web;

import java.io.File;
import java.util.List;

import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.Consumes;
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
import com.mysema.edith.dto.DocumentNoteTO;
import com.mysema.edith.dto.DocumentTO;
import com.mysema.edith.dto.NoteCommentTO;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.dto.SelectionTO;
import com.mysema.edith.services.ContentRenderer;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.DocumentNoteService;
import com.mysema.edith.services.NoteDao;
import com.sun.jersey.multipart.FormDataParam;

@Transactional
@Path("/documents")
@Produces(MediaType.APPLICATION_JSON)
public class DocumentsResource extends AbstractResource<DocumentTO>{

    private final DocumentDao dao;

    private final DocumentNoteDao documentNoteDao;
    
    private final DocumentNoteService documentNoteService;

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
            DocumentNoteService documentNoteService,
            NoteDao noteDao,
            ContentRenderer renderer) {
        this.dao = dao;
        this.documentNoteDao = documentNoteDao;
        this.documentNoteService = documentNoteService;
        this.noteDao = noteDao;
        this.renderer = renderer;
    }

    @Override
    @GET @Path("{id}")
    public DocumentTO getById(@PathParam("id") Long id) {
        return convert(dao.getById(id), new DocumentTO());
    }

    @GET @Path("{id}/document-notes")
    public List<DocumentNoteTO> getDocumentNotes(@PathParam("id") Long id) {
        List<DocumentNote> docNotes = documentNoteDao.getOfDocument(id);
        return convert(docNotes, DocumentNoteTO.class);
    }
    
    @POST @Path("{id}/document-notes")
    public DocumentNoteTO create(@PathParam("id") Long docId, SelectionTO sel) {
        Document doc = dao.getById(docId);
        DocumentNote documentNote;
        if (sel.getNoteId() != null) {
            Note note = noteDao.getById(sel.getNoteId());
            documentNote = documentNoteService.attachNote(note, doc, sel.getText());
        } else {
            documentNote = documentNoteService.attachNote(doc, sel.getText());
        }               
        return convert(documentNote, new DocumentNoteTO());
    }

    @PUT @Path("{id}/document-notes/{docnote-id}")
    public DocumentNoteTO update(@PathParam("docnote-id") Long docNoteId, SelectionTO sel) {
        DocumentNote documentNote = documentNoteService.getById(docNoteId);
        return convert(documentNoteService.updateNote(documentNote, sel.getText()), new DocumentNoteTO());
    }

    @Override
    @POST
    public DocumentTO create(DocumentTO info) {
        return convert(dao.save(convert(info, new Document())), new DocumentTO());
    }

    @Override
    @PUT @Path("{id}")
    public DocumentTO update(@PathParam("id") Long id, DocumentTO info) {
        Document entity = dao.getById(id);
        if (entity == null) {
            throw new RuntimeException("Entity not found");
        }
        return convert(dao.save(convert(info, entity)), new DocumentTO());
    }

    @Override
    @DELETE @Path("{id}")
    public void delete(@PathParam("id") Long id) {
        dao.remove(id);
    }
    
    @POST 
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public DocumentTO addDocument(@FormDataParam("path") String path, @FormDataParam("file") File file) {
        return convert(dao.addDocument(path, file), new DocumentTO());
    }

    @POST @Path("from-zip")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public List<DocumentTO> addDocumentsFromZip(@FormDataParam("path") String path, @FormDataParam("file") File file) {
        return convert(dao.addDocumentsFromZip(path, file), DocumentTO.class);
    }

    @GET
    @Path("{id}/raw")
    public void getRawDocument(
            @Context HttpServletResponse response,
            @PathParam("id") Long id) throws Exception {
    	response.setContentType("text/html; charset=utf-8");
        renderer.renderDocument(dao.getById(id), factory.createXMLStreamWriter(response.getWriter()));
    }

    @GET
    @Path("{id}/note-comments")
    public List<NoteCommentTO> getLatestComments(@PathParam("id") Long id) {
        List<NoteComment> noteComments = dao.getNoteComments(id, 3);
        // TODO: Remove once there is an UI implementation for adding comments
        if (noteComments.isEmpty()) {
            Document document = dao.getDocumentForPath(documentRoot + "/Nummisuutarit rakenteistettuna.xml");
            String element = "play-act-sp2-p";
            String text = "sun ullakosta ottaa";

            try {
                DocumentNote docNote = documentNoteService.attachNote(new Note(), document,
                        new SelectedText(element, element, text));
                noteDao.createComment(docNote.getNote(), "Yay");
                noteDao.createComment(docNote.getNote(), "Hay");
                noteDao.createComment(docNote.getNote(), "Bay");
            } catch (Exception e) {
                System.err.println(e);
            }
            noteComments = dao.getNoteComments(document.getId(), 3);
        }
        return convert(noteComments, NoteCommentTO.class);
    }
}
