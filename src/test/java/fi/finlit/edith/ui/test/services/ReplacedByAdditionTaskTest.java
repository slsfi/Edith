package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.ui.services.tasks.ReplacedByAdditionTask;

public class ReplacedByAdditionTaskTest extends AbstractServiceTest{

    @Inject
    private SessionFactory sessionFactory;

    @Inject
    private ReplacedByAdditionTask task;

    @Test
    public void Run() {
        Session session = sessionFactory.openSession();
        Note note1 = new Note();
        note1.setLemma("lemma1");
        Note note2 = new Note();
        note2.setLemma("lemma2");

        DocumentNote docNote11 = createDocumentNote(note1, "1", 1);
        DocumentNote docNote12 = createDocumentNote(note1, "1", 2);
        DocumentNote docNote13 = createDocumentNote(note1, "2", 1);
        DocumentNote docNote14 = createDocumentNote(note1, "2", 2);
        DocumentNote docNote21 = createDocumentNote(note2, "3", 1);
        DocumentNote docNote22 = createDocumentNote(note2, "3", 2);
        DocumentNote docNote23 = createDocumentNote(note2, "4", 1);
        DocumentNote docNote24 = createDocumentNote(note2, "4", 2);

        session.saveAll(note1, note2, docNote11, docNote12, docNote13, docNote14, docNote21, docNote22, docNote23, docNote24);
        session.flush();
        session.clear();

        task.run();

        docNote11 = session.getById(docNote11.getId(), DocumentNote.class);
        docNote12 = session.getById(docNote12.getId(), DocumentNote.class);
        docNote13 = session.getById(docNote13.getId(), DocumentNote.class);
        docNote14 = session.getById(docNote14.getId(), DocumentNote.class);
        docNote21 = session.getById(docNote21.getId(), DocumentNote.class);
        docNote22 = session.getById(docNote22.getId(), DocumentNote.class);
        docNote23 = session.getById(docNote23.getId(), DocumentNote.class);
        docNote24 = session.getById(docNote24.getId(), DocumentNote.class);;

        assertEquals(docNote12, docNote11.getReplacedBy());
        assertNull(docNote12.getReplacedBy());
        assertEquals(docNote14, docNote13.getReplacedBy());
        assertNull(docNote14.getReplacedBy());

        assertEquals(docNote22, docNote21.getReplacedBy());
        assertNull(docNote22.getReplacedBy());
        assertEquals(docNote24, docNote23.getReplacedBy());
        assertNull(docNote24.getReplacedBy());

    }

    private DocumentNote createDocumentNote(Note note, String localId, long created){
        DocumentNote docNote = new DocumentNote();
        docNote.setNote(note);
        docNote.setLocalId(localId);
        docNote.setCreatedOn(created);
        return docNote;
    }

}
