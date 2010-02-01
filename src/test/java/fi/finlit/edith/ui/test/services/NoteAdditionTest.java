package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.ui.services.DocumentRepositoryImpl;

/**
 * TEIManipulationTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteAdditionTest extends AbstractServiceTest{

    @Inject @Symbol(ServiceTestModule.TEST_DOCUMENT_FILE_KEY)
    private String testDocument;

    @InjectService("DocumentRepositoryImpl")
    private DocumentRepositoryImpl documentRepo;

    private InputStream source;

    private File targetFile;

    private OutputStream target;

    private String localId;

    private final XMLInputFactory inFactory = XMLInputFactory.newInstance();

    private final XMLOutputFactory outFactory = XMLOutputFactory.newInstance();

    @Before
    public void setUp() throws SVNException, IOException{
        source = new FileInputStream(new File(testDocument));
        targetFile = File.createTempFile("test", null);
        target = new FileOutputStream(targetFile);
        localId = UUID.randomUUID().toString();
    }

    @After
    public void tearDown() throws Exception {
        if (targetFile != null) {
            targetFile.delete();
        }
        source.close();
        target.close();
    }

    @Test
    public void addNote_for_p() throws Exception{
        String element = "act1-sp2";
        String text = "sun ullakosta ottaa";
        documentRepo.addNote(inFactory.createXMLEventReader(source), outFactory
                .createXMLEventWriter(target), element, element, text, localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains(start(localId) + text + end(localId)));
    }

    @Test
    public void addNote_for_speaker() throws Exception{
        String element = "act1-sp1";
        String text = "Esko.";
        documentRepo.addNote(inFactory.createXMLEventReader(source), outFactory
                .createXMLEventWriter(target), element, element, text, localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains("<speaker>" + start(localId) + text + end(localId) + "</speaker>"));
    }

    @Test
    public void addNote_multiple_elements() throws Exception{
        String start = "act1-sp2";
        String end = "act1-sp3";
        String text = "ja polvip\u00F6ksyt. Esko.";
        documentRepo.addNote(inFactory.createXMLEventReader(source), outFactory
                .createXMLEventWriter(target), start, end, text, localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("Esko." + end(localId)));
    }

    @Test
    public void addNote_multiple_elements_2() throws Exception{
        String start = "act1-sp2";
        String end = "act1-sp3";
        String text = "ja polvip\u00F6ksyt. Esko. (panee ty\u00F6ns\u00E4 pois).";
        documentRepo.addNote(inFactory.createXMLEventReader(source), outFactory
                .createXMLEventWriter(target), start, end, text, localId);

        String content = FileUtils.readFileToString(targetFile, "UTF-8");
        assertTrue(content.contains(start(localId) + "ja polvip\u00F6ksyt."));
        assertTrue(content.contains("(panee ty\u00F6ns\u00E4 pois)." + end(localId)));
    }

    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
