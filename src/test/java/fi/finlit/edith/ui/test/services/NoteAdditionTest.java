package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.ui.services.DocumentRepositoryImpl;
import fi.finlit.edith.ui.services.SubversionService;

/**
 * TEIManipulationTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteAdditionTest extends AbstractServiceTest{
    
    @Inject @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;
    
    @Inject
    private SubversionService svnService;
    
    @Inject
    private NoteRepository noteRepo;
    
    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_FILE_KEY)
    private String testDocument;
    
    private DocumentRepositoryImpl documentRepo;
    
    private File source, target;
    
    private String localId;
    
    @Before
    public void setUp() throws SVNException, IOException{
        documentRepo = new DocumentRepositoryImpl(documentRoot, svnService, noteRepo);
        source = new File(testDocument);
        target = File.createTempFile("test", null);
        localId = UUID.randomUUID().toString();
    }
    
    @After
    public void tearDown(){
        if (target != null){
            target.delete();    
        }        
    }
    
    @Test
    public void addNote_for_p() throws Exception{              
        String element = "act1-sp2";        
        String text = "sun ullakosta ottaa";
        documentRepo.addNote(source, target, element, element, text, localId);
        
        String content = FileUtils.readFileToString(target, "UTF-8");       
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }
    
    @Test
    public void addNote_for_speaker() throws Exception{
        String element = "act1-sp1";
        String text = "Esko.";
        documentRepo.addNote(source, target, element, element, text, localId);
        
        String content = FileUtils.readFileToString(target, "UTF-8");
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId) + "</speaker>"));
    }
    
    @Test
    public void addNote_multiple_elements() throws Exception{
        String start = "act1-sp2";
        String end = "act1-sp3";
        String text = "ja polvip\u00F6ksyt. Esko.";
        documentRepo.addNote(source, target, start, end, text, localId);
        
        String content = FileUtils.readFileToString(target, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }
    
    @Test
    public void addNote_multiple_elements_2() throws Exception{
        String start = "act1-sp2";
        String end = "act1-sp3";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        documentRepo.addNote(source, target, start, end, text, localId);
        
        String content = FileUtils.readFileToString(target, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }
    
    
    private static final String start(String localId){
        return "<anchor xml:id=\"start" + localId+"\"/>";
    }
    
    private static final String end(String localId){
        return "<anchor xml:id=\"end" + localId+"\"/>";
    }
    
    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
