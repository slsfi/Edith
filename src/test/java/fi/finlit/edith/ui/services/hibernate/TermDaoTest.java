package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.sql.domain.TermLanguage;
import fi.finlit.edith.ui.services.TermDao;

public class TermDaoTest extends AbstractHibernateTest {
    @Inject
    private TermDao termDao;

    @Test
    public void Find_By_Basic_Form_No_Results() {
        assertTrue(termDao.findByBasicForm("foobar").isEmpty());
    }

    @Test
    public void Find_By_Basic_Form() {
        Term term = new Term();
        term.setBasicForm("bar");
        term.setLanguage(TermLanguage.FINNISH);
        term.setMeaning("There is no meaning.");
        termDao.save(term);
        List<Term> results = termDao.findByBasicForm("bar");
        assertFalse(results.isEmpty());
        assertEquals(term.getBasicForm(), results.get(0).getBasicForm());
        assertEquals(term.getLanguage(), results.get(0).getLanguage());
        assertEquals(term.getMeaning(), results.get(0).getMeaning());
    }

    @Test
    public void Find_By_Start_Of_Basic_Form_Limit_2() {
        Term term = new Term();
        term.setBasicForm("bar");
        termDao.save(term);
        Term term2 = new Term();
        term2.setBasicForm("barche");
        termDao.save(term2);
        Term term3 = new Term();
        term3.setBasicForm("barzai");
        termDao.save(term3);
        List<Term> results = termDao.findByStartOfBasicForm("bar", 2);
        assertEquals(2, results.size());
    }

    @Test
    public void Remove(){
        Term term = new Term();
        term.setBasicForm("bar");
        termDao.save(term);

        assertNotNull(termDao.getById(term.getId()));
        termDao.remove(term.getId());
        assertNull(termDao.getById(term.getId()));
    }

}
