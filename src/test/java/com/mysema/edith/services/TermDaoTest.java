package com.mysema.edith.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.List;

import org.junit.Test;

import com.google.inject.Inject;
import com.google.inject.persist.Transactional;
import com.mysema.edith.domain.Term;


@Transactional
public class TermDaoTest extends AbstractHibernateTest {
    @Inject
    private TermDao termDao;

    @Test
    public void Find_By_Start_Of_Basic_Form_Limit_2() {
        Term term = new Term();
        term.setBasicForm("bar");
        getEntityManager().persist(term);
        Term term2 = new Term();
        term2.setBasicForm("barche");
        getEntityManager().persist(term2);
        Term term3 = new Term();
        term3.setBasicForm("barzai");
        getEntityManager().persist(term3);
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
        getEntityManager().persist(term);

        assertNotNull(termDao.getById(term.getId()));
        termDao.remove(term.getId());
        assertNull(termDao.getById(term.getId()));
    }

}
