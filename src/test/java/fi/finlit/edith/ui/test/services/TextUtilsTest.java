package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;

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

}
