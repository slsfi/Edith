/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.util;

import java.util.Collection;
import java.util.List;
import java.util.regex.Pattern;

import com.google.common.base.Joiner;
import com.google.common.base.Splitter;
import com.google.common.base.Strings;
import com.google.common.collect.Lists;

public final class StringUtils {
    
    private static final Splitter WHITESPACE_SPLITTER = Splitter.on(Pattern.compile("\\s+"));

    private static final Joiner EMPTY_JOINER = Joiner.on("");
    
    public static String[] split(String str) {
        if (str == null) {
            return null;
        } else if (str.trim().isEmpty()) {
            return new String[0];
        } else {
            List<String> list = Lists.newArrayList(WHITESPACE_SPLITTER.split(str.trim())); 
            return list.toArray(new String[list.size()]);    
        }        
    }
    
    public static String join(Collection<?> coll, String sep) {
        return Joiner.on(sep).join(coll);
    }
    
    public static String join(Collection<?> coll) {
        return EMPTY_JOINER.join(coll);
    }
    
    public static boolean isBlank(String str) {
        return str == null || str.trim().isEmpty();
    }

    public static int countMatches(String str, String substring) {
        if (Strings.isNullOrEmpty(str) || Strings.isNullOrEmpty(substring)) {
            return 0;
        } else {
            int matches = 0;
            int offset = 0;
            while (str.indexOf(substring, offset) >= 0) {
                matches++;
                offset = str.indexOf(substring, offset) + substring.length();
            }
            return matches;    
        }               
    }
    
    private StringUtils(){}

    
}
