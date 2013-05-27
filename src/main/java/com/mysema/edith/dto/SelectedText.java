/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.regex.Pattern;

public class SelectedText {

    private String selection;

    private String startNode, endNode;

    private int startCharIndex = 0, endCharIndex = 0;

    private static final Pattern HYPHEN = Pattern.compile("-");

    public SelectedText() {
    }

    public SelectedText(String startNode, String endNode, int startCharIndex, int endCharIndex, String selection) {
        this.startNode = startNode;
        this.endNode = endNode;
        this.startCharIndex = startCharIndex;
        this.endCharIndex = endCharIndex;
        this.selection = selection;
    }

    public SelectedText(String startId, String endId, String selection) {
        this(startId, endId, 0, 0, selection);
    }

    public String getEndNode() {
        return endNode;
    }

    public int getEndCharIndex() {
        return endCharIndex;
    }

    public String getSelection() {
        return selection;
    }

    public String getStartNode() {
        return startNode;
    }

    public int getStartCharIndex() {
        return startCharIndex;
    }

    public void setSelection(String selection) {
        this.selection = selection;
    }

    public void setEndNode(String endId) {
        this.endNode = endId;
    }

    public void setEndCharIndex(int endIndex) {
        this.endCharIndex = endIndex;
    }

    public void setStartNode(String startId) {
        this.startNode = startId;
    }

    public void setStartCharIndex(int startIndex) {
        this.startCharIndex = startIndex;
    }

    public boolean isValid() {
        return hasSelection() && hasStart() && hasEnd();
    }

    private boolean hasSelection() {
        return selection != null && selection.trim().length() > 0;
    }

    private boolean hasStart() {
        return startNode != null && startNode.trim().length() > 0;
    }

    private boolean hasEnd() {
        return endNode != null && endNode.trim().length() > 0;
    }

    public char getFirstChar() {
        return selection.charAt(0);
    }

    public char getLastChar() {
        return selection.charAt(selection.length() - 1);
    }

    public boolean isStartChildOfEnd() {
        return startNode.startsWith(endNode) && endNode.length() < startNode.length();
    }

    public int howDeepIsStartInEnd() {
        return howDeepIsElementInElement(startNode, endNode);
    }

    public int howDeepIsEndInStart() {
        return howDeepIsElementInElement(endNode, startNode);
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
        return endNode.startsWith(startNode) && endNode.length() > startNode.length();
    }

    @Override
    public String toString() {
        StringBuffer buffer = new StringBuffer();
        buffer.append(startNode + "[" + startCharIndex + "] , ");
        buffer.append(endNode + "[" + endCharIndex + "] : ");
        buffer.append(selection);
        return buffer.toString();
    }
}