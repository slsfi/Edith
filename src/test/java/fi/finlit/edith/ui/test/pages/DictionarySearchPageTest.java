package fi.finlit.edith.ui.test.pages;

import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.ui.pages.DictionarySearchPage;
import fi.finlit.edith.ui.test.services.AbstractServiceTest;


public class DictionarySearchPageTest extends AbstractServiceTest{
    
    @Autobuild
    @Inject
    private DictionarySearchPage dictionarySearchPage;
    
    @Test
    public void setupRender(){
        dictionarySearchPage.setupRender();
    }
    
    @Test
    @Ignore
    public void getLongTexts() {
        // TODO : inject note
        dictionarySearchPage.getLongTexts();
    }

}
