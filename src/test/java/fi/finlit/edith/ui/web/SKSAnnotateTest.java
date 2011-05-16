package fi.finlit.edith.ui.web;

import org.junit.Test;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SKSAnnotateTest extends AbstractSeleniumTest {
    
    @Test
    public void Submit_Annotate_Form() {
        login("lassi", "lassi");        
        get("/document/annotate/12");

        // TODO
    }


    @Override
    public WebappStarter starter() {
        return Setups.SKS_TEST;
    }

}
