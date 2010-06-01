package fi.finlit.edith.ui.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.Term;
import fi.finlit.edith.domain.TermLanguage;
import fi.finlit.edith.domain.TermRepository;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;

public class TermRepositoryImplTest extends AbstractServiceTest {
    @Inject
    private TermRepository repository;

    @Test
    public void Find_By_Basic_Form_No_Results() {
        assertTrue(repository.findByBasicForm("foobar").isEmpty());
    }

    @Test
    public void Find_By_Basic_Form() {
        Term term = new Term();
        term.setBasicForm("bar");
        term.setLanguage(TermLanguage.FINNISH);
        term.setMeaning("There is no meaning.");
        repository.save(term);
        List<Term> results = repository.findByBasicForm("bar");
        assertFalse(results.isEmpty());
        assertEquals(term.getBasicForm(), results.get(0).getBasicForm());
        assertEquals(term.getLanguage(), results.get(0).getLanguage());
        assertEquals(term.getMeaning(), results.get(0).getMeaning());
    }

    @Test
    public void Find_By_Start_Of_Basic_Form_Limit_2() {
        Term term = new Term();
        term.setBasicForm("bar");
        repository.save(term);
        Term term2 = new Term();
        term2.setBasicForm("barche");
        repository.save(term2);
        Term term3 = new Term();
        term3.setBasicForm("barzai");
        repository.save(term3);
        List<Term> results = repository.findByStartOfBasicForm("bar", 2);
        assertEquals(2, results.size());
    }

    @Override
    protected Class<?> getServiceClass() {
        return null;
    }

}
