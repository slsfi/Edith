/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
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
    public void getStartIndex() {
        assertEquals(0, TextUtils.getStartIndex("abcdefgh", "abc"));
        assertEquals(9, TextUtils.getStartIndex("foo bar, foo\tbaz", "foo baz"));
        assertEquals(-1, TextUtils.getStartIndex("f b, f\tc", "f d"));
        assertEquals(1, TextUtils.getStartIndex("abcdefgh", "bc"));
        assertEquals(6, TextUtils.getStartIndex("abcdefgh", "gh ij"));
        assertEquals(-1, TextUtils.getStartIndex("abcdefgh", "0 abcd"));
    }

    @Test
    public void getEndIndex() {
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "abc"));
        assertEquals(7, TextUtils.getEndIndex("foo\tbar, foo baz", "foo bar"));
        assertEquals(16, TextUtils.getEndIndex("foo\tbaz, foo\tbar, foo baz", "foo bar"));
        assertEquals(7, TextUtils.getEndIndex("foo bar, foo\tbar, foo baz", "foo bar"));
        assertEquals(19, TextUtils.getEndIndex("foo bat, foo\t\t\t\tbar, foo baz", "foo bar"));
        assertEquals(-1, TextUtils.getEndIndex("b", "a"));
        assertEquals(-1, TextUtils.getEndIndex("b b", "b a"));
        assertEquals(1, TextUtils.getEndIndex("foo bat, foo\t\t\t\tba, foo baz", "foo baf"));
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "bc"));
        assertEquals(3, TextUtils.getEndIndex("abcdefgh", "123 abc"));
    }

    @Test
    public void longText() {
        StringBuilder text = new StringBuilder();
        text.append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        text.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");

        String startText = "Jaana! K\u00E4skih\u00E4n \u00E4itini, l\u00E4hteiss\u00E4ns\u00E4 kyl\u00E4\u00E4n, ett\u00E4 matkalle, nimitt\u00E4in";
        String endText = "koska h\u00E4n takaisin palajaa?";
        assertEquals(startText.indexOf("matkalle"), TextUtils.getStartIndex(startText, text
                .toString()));
        assertEquals(endText.indexOf("ajaa?"), TextUtils.getEndIndex(endText, text.toString()));

    }

    @Test
    public void longText2() {
        StringBuilder noteLongText = new StringBuilder();
        noteLongText
                .append("matkalle, nimitt\u00E4in h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ");
        noteLongText.append("ja sulhais-vaatteisin puettuna olen, koska h\u00E4n takaisin pal");

        String xmlCharacters = "Jaana! K\u00E4skih\u00E4n \u00E4itini, l\u00E4hteiss\u00E4ns\u00E4 kyl\u00E4\u00E4n, ett\u00E4 matkalle, nimitt\u00E4in\n"
                + "          h\u00E4\u00E4retkelleni, itsi\u00E4ni sonnustan, ja sulhais-vaatteisin puettuna olen,\n"
                + "          koska h\u00E4n takaisin palajaa?";

        assertEquals(xmlCharacters.indexOf("matkalle"), TextUtils.getStartIndex(xmlCharacters,
                noteLongText.toString()));
        assertEquals(xmlCharacters.indexOf("ajaa?"), TextUtils.getEndIndex(xmlCharacters,
                noteLongText.toString()));
    }

    @Test
    public void longText3() {
        String noteLongText = "ää jälkeensäätös, ja minä paitsi jään. Mutta riksit monet tuhannet " +
        		"eivät ole Kristoni verranvastaiset, kuin vaan hänen kerran omakseni saan" +
        		" ja omaksensa joudun minä; ja nytpä, luulen, kohta toivoni p";
        String xmlCharacters = "\t\t\t\t\t\t\tEnnen minua hän nai ja on hänellä siis oikeus periä viisisataa riksiä," +
        		" niinkuin määrää jälkeensäätös, ja minä paitsi jään. Mutta riksit monet" +
        		" tuhannet eivät ole Kristoni verranvastaiset, kuin vaan hänen kerran omakseni" +
        		" saan ja omaksensa joudun minä; ja nytpä, luulen, kohta toivoni\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t\t perille pääsen. ";
        assertEquals(xmlCharacters.indexOf("ää jälkeensäätös"), TextUtils.getStartIndex(xmlCharacters,
                noteLongText));
        assertEquals(xmlCharacters.indexOf("erille pääsen. "), TextUtils.getEndIndex(xmlCharacters,
                noteLongText));
    }

    @Test
    public void longText4() {
        String noteLongText = "sonnustan, ja sulhais-vaatteisin puettuna olen, koska hän takaisin palajaa?\n" +
        		" Jaana.\t Ja käski sun ullakosta ottaa päälles isäs hännystakin, hatun, punaisen" +
        		" västin ja polvi";
        String xmlCharacters = "Esko\t Jaana! Käskihän äitini, lähteissänsä kylään, että matkalle," +
        		" nimittäin hääretkelleni, itsiäni sonnustan, ja sulhais-vaatteisin puettuna" +
        		" olen, koska hän takaisin palajaa?\n\n\n Jaana.\t Ja käski sun ullakosta ottaa" +
        		" päälles isäs hännystakin, hatun, punaisen västin ja polvipöksyt.";
        assertEquals(xmlCharacters.indexOf("sonnustan, ja"), TextUtils.getStartIndex(xmlCharacters,
                noteLongText));
        assertEquals(xmlCharacters.indexOf("pöksyt."), TextUtils.getEndIndex(xmlCharacters,
                noteLongText));
    }
}
