package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * NoteRevisionRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRevisionRepositoryTest extends AbstractServiceTest{
    
    // TODO : as symbol
    static final String TEST_DOCUMENT_SVNPATH = "/documents/trunk/Nummisuutarit rakenteistettuna-annotoituna.xml";
    
    @Inject
    private NoteRepository noteRepo;
    
    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Inject
    private DocumentRepository documentRepo;
    
    @Test
    public void getByLocalId() throws SVNException{
        Document document = documentRepo.getDocumentForPath(TEST_DOCUMENT_SVNPATH);
        List<Long> revisions = documentRepo.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).longValue();
        
        noteRepo.createNote(document, latestRevision, "1", "lähtee häihinsä", "lähtee häihinsä Mikko Vilkastuksen");
        noteRepo.createNote(document, latestRevision, "2", "käskyn annoit", "koska suutarille käskyn käräjiin annoit, saadaksesi naimalupaa.");
        noteRepo.createNote(document, latestRevision, "3", "tulee", "tulee, niin seisoo säätös-kirjassa.");
        noteRepo.createNote(document, latestRevision, "4", "määrätty", "kummallenkin määrätty, niin emmepä tietäisi tässä");
        
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "1"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "2"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "3"));
        assertNotNull(noteRevisionRepo.getByLocalId(document, latestRevision, "4"));
    }

    @Test
    public void queryNotes(){
        // TODO
    }
}
