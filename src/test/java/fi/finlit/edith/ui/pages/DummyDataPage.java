/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import java.io.File;
import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * TestDataPage provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DummyDataPage {
    
    static final String TEST_DOCUMENT_SVNPATH = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";
    
    static final String TEST_NOTES_FILEPATH = "etc/demo-material/notes/nootit.xml";
    
    @Inject
    private NoteRepository noteRepository;
    
    @Inject
    private DocumentRepository documentRepository;
    
    @Inject
    private AdminService adminService;

    @Secured("ROLE_USER")
    void onActivate(){

    }
    
    void onAddNoteSearchTestData() throws Exception{
        noteRepository.importNotes(new File(TEST_NOTES_FILEPATH));
    }
    
    void onAddAnnotateTestData() throws SVNException{
        Document document = documentRepository.getDocumentForPath(TEST_DOCUMENT_SVNPATH);
        List<Long> revisions = documentRepository.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).longValue();
        
        noteRepository.createNote(document, latestRevision, "1", "lähtee häihinsä", "lähtee häihinsä Mikko Vilkastuksen");
        noteRepository.createNote(document, latestRevision, "2", "käskyn annoit", "koska suutarille käskyn käräjiin annoit, saadaksesi naimalupaa.");
        noteRepository.createNote(document, latestRevision, "3", "tulee", "tulee, niin seisoo säätös-kirjassa.");
        noteRepository.createNote(document, latestRevision, "4", "määrätty", "kummallenkin määrätty, niin emmepä tietäisi tässä");
    }
    
    void onRemoveNotes(){
        adminService.removeNotesAndTerms();
    }

}
