package fi.finlit.edith.ui.web;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SKSAnnotateTest extends AbstractSeleniumTest {
    
    @Test
    public void Submit_Annotate_Form() {
        login("lassi", "lassi");        
        get("/document/annotate/12");
    
        WebElement element = findElement(By.id("play-act-sp7-p"));
        // TODO
    }


    @Override
    public WebappStarter starter() {
        return Setups.SKS_TEST;
    }

}
