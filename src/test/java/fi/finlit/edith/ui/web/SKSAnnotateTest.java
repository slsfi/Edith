package fi.finlit.edith.ui.web;

import java.util.UUID;

import org.junit.Ignore;
import org.junit.Test;
import org.openqa.selenium.By;

import com.mysema.commons.jetty.WebappStarter;

import fi.finlit.edith.Setups;

//TODO Fix SKS to new UI
@Ignore
public class SKSAnnotateTest extends AbstractSeleniumTest {

    @Test
    public void GetForm_FillForm_And_Submit() {
        login("lassi", "lassi");
        get("/addnote/12/play-act-sp53-p/play-act-sp53-p/minusta");
        get("/document/annotate/12"); // nummisuutarit_simp.xml

        // get form
        findElement(By.cssSelector("a.notelink")).click();
        By noteFormSubmit = By.cssSelector("input.noteFormSubmit");
        wait(element(noteFormSubmit));

        // fill form
        String random = UUID.randomUUID().toString();
        findElement(By.cssSelector("div.lemma input")).sendKeys("lemma-"+random);
        findElement(By.cssSelector("div.lemmaMeaning textarea")).sendKeys("lemmaMeaning-"+random);
//        findElement(By.cssSelector("div.basicForm input")).sendKeys("basicForm-"+random);
//        findElement(By.cssSelector("div.basicFormMeaning input")).sendKeys("basicFormMeaning-"+random);

        // submit
        findElement(noteFormSubmit).click();

    }

    @Test
    public void Submit_NoteForm_As_New() {
        login("lassi", "lassi");
        get("/addnote/12/play-act-sp53-p/play-act-sp53-p/minusta");
        get("/document/annotate/12"); // nummisuutarit_simp.xml

        // get form
        findElement(By.cssSelector("a.notelink")).click();
        By noteFormSubmit = By.cssSelector("input.noteFormSubmit");
        wait(element(noteFormSubmit));

        // fill form
        findElement(By.cssSelector(".saveAsNew input")).click();

        // submit
        findElement(noteFormSubmit).click();

    }

    @Override
    public WebappStarter starter() {
        return Setups.SKS_TEST;
    }

}
