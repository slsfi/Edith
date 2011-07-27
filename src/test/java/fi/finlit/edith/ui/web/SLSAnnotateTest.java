package fi.finlit.edith.ui.web;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.Mouse;
import org.openqa.selenium.interactions.internal.MouseAction;
import org.openqa.selenium.remote.RemoteWebElement;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

public class SLSAnnotateTest extends AbstractSeleniumTest {

    @Test
    public void Documents_Texts_Are_In_Swedish_Locale() {
        login("lassi", "lassi");
        get("/documents");
        
        assertTitle("Dokument");
        assertLink("Kommentarer");
        assertLink("Hämta kommentarer");
        assertLink("Instructioner");
    }
    
    @Test
    public void Annotate_Texts_Are_In_Swedish_Locale() throws Exception {
        login("lassi", "lassi");
        get("/documents");
        wait(element(By.linkText("Nummisuutarit rakenteistettuna.xml")));

        findByLinkText("Nummisuutarit rakenteistettuna.xml").click();
        
        findElement("#to_annotate").click();
        
        assertTitleNot("Application Exception");
        assertContainsText("Kommentarvy");
        assertLink("Textvy");
        assertLink("Publiceringsvy");
    }

    @Test
    public void New_Note_Can_Be_Created() {
        // TODO
        
    }

    @Override
    public String locales() {
        return "sv,fi,en";
    }

    @Override
    public WebappStarter starter() {
        return Setups.SLS_TEST;
    }

}
