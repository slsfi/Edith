/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.regex.Pattern;

public class SelectedText {

    private String selection;

    private String startId, endId;

    private int startIndex = 0, endIndex = 0;

    private static final Pattern HYPHEN = Pattern.compile("-");
    
    public SelectedText() {
    }

    public SelectedText(String startId, String endId, int startIndex, int endIndex, String selection) {
        this.startId = startId;
        this.endId = endId;
        this.startIndex = startIndex;
        this.endIndex = endIndex;
        this.selection = selection;
    }

    public SelectedText(String startId, String endId, String selection) {
        this(startId, endId, 0, 0, selection);
    }

    public String getEndId() {
        return endId;
    }

    public int getEndIndex() {
        return endIndex;
    }

    public String getSelection() {
        return selection;
    }

    public String getStartId() {
        return startId;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setEndId(String endId) {
        this.endId = endId;
    }

    public void setEndIndex(int endIndex) {
        this.endIndex = endIndex;
    }

    public void setStartId(String startId) {
        this.startId = startId;
    }

    public void setStartIndex(int startIndex) {
        this.startIndex = startIndex;
    }

    public boolean isValid() {
        return hasSelection() && hasStart() && hasEnd();
    }

    private boolean hasSelection() {
        return selection != null && selection.trim().length() > 0;
    }

    private boolean hasStart() {
        return startId != null && startId.trim().length() > 0;
    }

    private boolean hasEnd() {
        return endId != null && endId.trim().length() > 0;
    }

//    public String getFirstWord() {
//        String[] words = StringUtils.split(selection);
//        return words[0];
//    }
//
//    public String getLastWord() {
//        String[] words = StringUtils.split(selection);
//        return words[words.length - 1];
//    }
    
    public char getFirstChar() {
        return selection.charAt(0);
    }
    
    public char getLastChar() {
        return selection.charAt(selection.length() - 1);
    }

    public boolean isStartChildOfEnd() {
        return startId.startsWith(endId) && endId.length() < startId.length();
    }

    public int howDeepIsStartInEnd() {
        return howDeepIsElementInElement(startId, endId);
    }

    public int howDeepIsEndInStart() {
        return howDeepIsElementInElement(endId, startId);
    }

    private int howDeepIsElementInElement(String el1, String el2) {
        int n = 0;
        String el1s[] = HYPHEN.split(el1);
        String el2s[] = HYPHEN.split(el2);
        for (int i = 0; i < el1s.length; ++i) {
            if (i < el2s.length) {
                if (!el1s[i].equals(el2s[i])) {
                    return -1;
                }
            } else {
                ++n;
            }
        }
        return n;
    }

    public boolean isEndChildOfStart() {
        return endId.startsWith(startId) && endId.length() > startId.length();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(startId + "[" + startIndex + "] , ");
        buffer.append(endId + "[" + endIndex + "] : ");
        buffer.append(selection);
        return buffer.toString();
    }
}