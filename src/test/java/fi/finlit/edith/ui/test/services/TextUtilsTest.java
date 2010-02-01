package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;

import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.ui.services.TextUtils;

/**
 * TextUtilsTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TextUtilsTest {
    
    @Test
    public void getStartIndex(){
        assertEquals(0, TextUtils.getStartIndex("abcdefgh", "abc"));
        assertEquals(1, TextUtils.getStartIndex("abcdefgh", "bc"));
        assertEquals(6, TextUtils.getStartIndex("abcdefgh", "ghij"));
    }
    
    @Test
    public void getEndIndex(){
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "abc"));
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "bc"));
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "123abc"));
    }

    @Test
    public void longText(){
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        
        String startText = "Jaana! K\u00E4skih\u00E4n \u00E4itini, l\u00E4hteiss\u00E4ns\u00E4 kyl\u00E4\u00E4n, ett\u00E4 matkalle, nimitt\u00E4in";
        String endText = "koska h\u00E4n takaisin palajaa?";
        assertEquals(startText.indexOf("matkalle"), TextUtils.getStartIndex(startText, text.toString()));
        assertEquals(endText.indexOf("ajaa?"), TextUtils.getEndIndex(endText, text.toString()));
        
    }
    
    @Test
    @Ignore
    public void longText2(){
        // FIXME
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");
        
        String elementText = "Jaana! K\u00E4skih\u00E4n \u00E4itini, l\u00E4hteiss\u00E4ns\u00E4 kyl\u00E4\u00E4n, ett\u00E4 matkalle, nimitt\u00E4in\n" +
        "          h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ja sulhais-vaatteisin puettuna olen,\n" +
        "          koska h\u00E4n takaisin palajaa?";
        
        assertEquals(elementText.indexOf("matkalle"), TextUtils.getStartIndex(elementText, text.toString()));
        assertEquals(elementText.indexOf("ajaa?"), TextUtils.getEndIndex(elementText, text.toString()));
    }
}
