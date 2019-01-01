/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.domain;

public class UrlElement implements ParagraphElement {

    private final String string;

    private String url;

    public UrlElement(String string) {
        this.string = string;
    }

    @Override
    public ParagraphElement copy() {
        UrlElement element = new UrlElement(string);
        element.setUrl(url);
        return element;
    }

    @Override
    public String toString() {
        return "<a" + (url == null ? "" : " href=\"" + url + "\"") + ">" + string + "</a>";
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

    public String getString() {
        return string;
    }
}