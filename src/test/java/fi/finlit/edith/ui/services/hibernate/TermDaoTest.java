package fi.finlit.edith.ui.services.hibernate;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.ui.services.TermDao;

public class TermDaoTest extends AbstractHibernateTest {
    @Inject
    private TermDao termDao;

    @Test
    public void Find_By_Start_Of_Basic_Form_Limit_2() {
        Term term = new Term();
        term.setBasicForm("bar");
        getSession().save(term);
        Term term2 = new Term();
        term2.setBasicForm("barche");
        getSession().save(term2);
        Term term3 = new Term();
        term3.setBasicForm("barzai");
        getSession().save(term3);
        List<Term> results = termDao.findByStartOfBasicForm("bar", 2);
        assertEquals(2, results.size());
    }

    @Test
    public void Save_And_Find() {
        Term term = new Term();
        term.setBasicForm("bar");
        termDao.save(term);
        List<Term> results = termDao.findByStartOfBasicForm("bar", 100);
        assertEquals(1, results.size());
    }

    @Test
    public void Remove(){
        Term term = new Term();
        term.setBasicForm("bar");
        getSession().save(term);

        assertNotNull(termDao.getById(term.getId()));
        termDao.remove(term.getId());
        assertNull(termDao.getById(term.getId()));
    }

}
