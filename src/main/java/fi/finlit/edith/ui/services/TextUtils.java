/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

/**
 * TextUtils provides
 *
 * @author tiwe
 * @version $Id$
 */
public class TextUtils {

    // TODO : change into service (interface + impl) -> TextMatchingService/Impl
    // TODO : make all regex code use precompiled Patterns

    public static int getStartIndex(String xmlCharacters, String noteLongText) {
        if (xmlCharacters.contains(noteLongText)) {
            int firstIndex = xmlCharacters.indexOf(noteLongText);
            int lastIndex = xmlCharacters.lastIndexOf(noteLongText);
            if (lastIndex == firstIndex) {
                return firstIndex;
            } else {
                // TODO : log error
                return lastIndex;
            }
        } else {
            for (int i = 0; i < xmlCharacters.length(); ++i) {
                if (xmlCharacters.charAt(i) == noteLongText.charAt(0)
                        && (xmlCharacters.substring(i).replaceAll("\\s+", " ").startsWith(
                                noteLongText) || noteLongText.startsWith(xmlCharacters.substring(i)
                                .replaceAll("\\s+", " ")))) {
                    return i;
                }
            }
            return -1;
        }
    }

    public static int getEndIndex(String xmlCharacters, String noteLongText) {
        if (xmlCharacters.contains(noteLongText)) {
            int firstIndex = xmlCharacters.indexOf(noteLongText);
            int lastIndex = xmlCharacters.lastIndexOf(noteLongText);
            if (lastIndex == firstIndex) {
                return firstIndex + noteLongText.length();
            } else {
                // TODO : log error
                return lastIndex + noteLongText.length();
            }
        } else if (xmlCharacters.replaceAll("\\s+", " ").contains(noteLongText)) {
            // TODO cleanup
            String[] words = noteLongText.split("\\s+");
            String word = words[words.length - 1];
            while (true) {
                int i = xmlCharacters.lastIndexOf(word) + word.length();
                if (i == -1) {
                    return -1;
                }
                char xc = xmlCharacters.charAt(i - 1);
                char nc = noteLongText.charAt(noteLongText.length() - 1);
                String xmlcharssubstring = xmlCharacters.substring(0, i).replaceAll("\\s+", " ");
                if (xc == nc
                        && (xmlcharssubstring.endsWith(noteLongText) || noteLongText
                                .endsWith(xmlcharssubstring))) {
                    return i;
                }
                // TODO This might have a bug even though none of the tests prove it
                // the original length probably should be taken into consideration as
                // in the finding of the start index.
                xmlCharacters = xmlCharacters.substring(0, i - 1);
            }
        } else {
            for (int i = xmlCharacters.length() - 1; 0 <= i; --i) {
                String nlt = noteLongText;
                String xmlc = xmlCharacters.substring(0, i + 1);
                if (xmlCharacters.charAt(i) == noteLongText.charAt(noteLongText.length() - 1)
                        && (xmlCharacters.substring(0, i).replaceAll("\\s+", " ").endsWith(
                                noteLongText.replaceAll("\\s+", " ")) || (noteLongText.replaceAll(
                                "\\s+", " ").endsWith(xmlCharacters.substring(0, i + 1).replaceAll(
                                "\\s+", " "))))) {
                    return i + 1;
                }
            }
        }
        return -1;
    }
}
