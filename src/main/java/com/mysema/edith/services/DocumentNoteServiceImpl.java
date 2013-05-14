package com.mysema.edith.services;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.dto.SelectedText;

/**
 * @author tiwe
 *
 */
@Transactional
public class DocumentNoteServiceImpl implements DocumentNoteService {
    
    private final AuthService authService;
    
    private final DocumentXMLDao xmlDao;
    
    private final DocumentNoteDao documentNoteDao;
    
    private final NoteDao noteDao;
    
    private final VersioningDao versioningDao;
    
    @Inject
    public DocumentNoteServiceImpl(AuthService authService, DocumentXMLDao documentXmlDao,
            DocumentNoteDao documentNoteDao, NoteDao noteDao, VersioningDao versioningDao) {
        this.authService = authService;
        this.xmlDao = documentXmlDao;
        this.documentNoteDao = documentNoteDao;
        this.noteDao = noteDao;
        this.versioningDao = versioningDao;
    }
    
    @Override
    public DocumentNote attachNote(Note note, Document document, final SelectedText selection) {
        // create stub
        final DocumentNote documentNote = new DocumentNote();
        documentNoteDao.save(documentNote);
        
        // add into XML
        final AtomicInteger position = new AtomicInteger(0);
        versioningDao.commit(document.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) throws IOException {
                        position.set(xmlDao.addNote(source, target, selection, documentNote));
                    }
                });

        // update
        DocumentNote updatedDocumentNote = noteDao.createDocumentNote(documentNote, note, document,
                selection.getSelection(), position.intValue());
        return updatedDocumentNote;
    }
    

    @Override
    public DocumentNote getById(long id) {
        return documentNoteDao.getById(id);
    }
    
    @Override
    public void removeDocumentNotes(Document document, final DocumentNote... documentNotes) {
        // remove from XML
        long revision;
        revision = versioningDao.commit(document.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        xmlDao.removeNotes(source, target, documentNotes);
                    }
                });

        // remove from database
        for (DocumentNote dn : documentNotes) {
            dn.setRevision(revision);
            documentNoteDao.remove(dn);
        }
    }
    
    @Override
    public void remove(long id) {
        DocumentNote documentNote = documentNoteDao.getById(id);
        removeDocumentNotes(documentNote.getDocument(), documentNote);
    }

    @Override
    public DocumentNote save(DocumentNote documentNote) {
        return documentNoteDao.save(documentNote);
    }
    
    @Override
    public DocumentNote updateNote(final DocumentNote documentNote, final SelectedText selection)
            throws IOException {
        Document doc = documentNote.getDocument();
        
        // update in XML
        long newRevision;
        final AtomicInteger position = new AtomicInteger(0);
        newRevision = versioningDao.commit(doc.getPath(), -1, authService.getUsername(),
                new UpdateCallback() {
                    @Override
                    public void update(InputStream source, OutputStream target) {
                        position.set(xmlDao.updateNote(source, target, selection, documentNote));
                    }
                });
        
        // update in database
        DocumentNote fetchedDocumentNote = documentNoteDao.getById(documentNote.getId());
        fetchedDocumentNote.setFullSelection(selection.getSelection());
        fetchedDocumentNote.setRevision(newRevision);
        fetchedDocumentNote.setPosition(position.intValue());
        return fetchedDocumentNote;
    }

}
