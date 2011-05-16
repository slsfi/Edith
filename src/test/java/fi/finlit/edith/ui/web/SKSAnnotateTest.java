package fi.finlit.edith.ui.web;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SKSAnnotateTest extends AbstractSeleniumTest {
    
    @Test
    @Ignore
    public void Submit_Annotate_Form() {
        login("lassi", "lassi");        
        get("/document/annotate/12"); // nummisuutarit_simp.xml

        WebElement element = findElement(By.id("play-act-sp53-p")); // Mitäs mamma nyt tykkää minusta
        dblClick(element);        
        findElement(By.id("createTermLink")).click();
    }
    
    @Override
    public WebappStarter starter() {
        return Setups.SKS_TEST;
    }

}
