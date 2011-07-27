package fi.finlit.edith.ui.test.pages;

import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.ui.pages.DictionarySearch;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;


public class DictionarySearchTest extends AbstractHibernateTest {
    
    @Autobuild
    @Inject
    private DictionarySearch dictionarySearchPage;
    
    @Test
    public void setupRender(){
        dictionarySearchPage.setupRender();
    }
    
    @Test
    @Ignore
    public void getLongTexts() {
        // TODO : inject note
        dictionarySearchPage.getFullSelections();
    }

}
