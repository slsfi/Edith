package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.LinkElement;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.Paragraph;
import fi.finlit.edith.domain.StringElement;

public class ParagraphPersistenceTest extends AbstractServiceTest{
    
    @Inject
    private SessionFactory sessionFactory;

    private Session session;
    
    @Override
    protected Class<?> getServiceClass() {
        return null;
    }
    
    @Before
    public void setUp(){
        session = sessionFactory.openSession();
    }
    
    @After
    public void tearDown() throws IOException{
        if (session != null){
            session.close();    
        }        
    }
    
    @Test
    public void test_same_Session(){
        Paragraph paragraph = newParagraph();
        session.save(paragraph);
        session.clear();
        
        Paragraph paragraph2 = session.getById(paragraph.getId(), Paragraph.class);
        assertEquals(3, paragraph2.getElements().size());
        assertEquals(paragraph.getElements(), paragraph2.getElements());
        assertNotNull(paragraph2.getElements().get(0));
    }

    @Test
    public void test_different_Session() throws IOException{
        Paragraph paragraph = newParagraph();
        session.save(paragraph);
        session.close();
        
        session = sessionFactory.openSession();
        Paragraph paragraph2 = session.getById(paragraph.getId(), Paragraph.class);
        assertEquals(3, paragraph2.getElements().size());
        assertEquals(paragraph.getElements(), paragraph2.getElements());
        assertNotNull(paragraph2.getElements().get(0));
    }
    
    @Test
    public void test_in_deep_path() throws IOException{
        Note note = new Note();
        DocumentNote documentNote = new DocumentNote();
        documentNote.setNote(note);
        Paragraph paragraph = newParagraph();
        note.setDescription(paragraph);
        session.save(note);
        session.save(documentNote);
        session.close();
        
        session = sessionFactory.openSession();
        DocumentNote documentNote2 = session.getById(documentNote.getId(), DocumentNote.class);
        Paragraph paragraph2 = documentNote2.getNote().getDescription();
        assertEquals(3, paragraph2.getElements().size());
        assertEquals(paragraph.getElements(), paragraph2.getElements());
        assertNotNull(paragraph2.getElements().get(0));
    }

    
    private Paragraph newParagraph() {
        Paragraph paragraph = new Paragraph();
        paragraph.addElement(new StringElement("a"));
        paragraph.addElement(new StringElement("b"));
        paragraph.addElement(new LinkElement("c"));
        return paragraph;
    }

}
