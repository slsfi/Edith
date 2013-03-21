/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.HashSet;
import java.util.Set;

import com.mysema.edith.domain.Document;
import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.TermLanguage;

public class NoteSearchTO {

    private Set<Document> documents = new HashSet<Document>();

    private Set<String> paths = new HashSet<String>();

    private Set<NoteType> noteTypes = new HashSet<NoteType>();

    private Set<NoteFormat> noteFormats = new HashSet<NoteFormat>();

    private Set<UserTO> creators = new HashSet<UserTO>();

    private TermLanguage language;

    private OrderBy orderBy;

    private boolean ascending = true;

    private boolean orphans = false;

    private boolean includeAllDocs = false;

    private Document currentDocument;

    private String fullText;

    public NoteSearchTO() {
    }

    public NoteSearchTO(Document document) {
        currentDocument = document;
    }

    public Set<Document> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Document> documents) {
        this.documents = documents;
    }

    public void setPaths(Set<String> paths) {
        this.paths = paths;
    }

    public Set<String> getPaths() {
        return paths;
    }

    public Set<NoteType> getNoteTypes() {
        return noteTypes;
    }

    public void setNoteTypes(Set<NoteType> noteTypes) {
        this.noteTypes = noteTypes;
    }

    public Set<NoteFormat> getNoteFormats() {
        return noteFormats;
    }

    public void setNoteFormats(Set<NoteFormat> noteFormats) {
        this.noteFormats = noteFormats;
    }

    public Set<UserTO> getCreators() {
        return creators;
    }

    public void setCreators(Set<UserTO> creators) {
        this.creators = creators;
    }

    public OrderBy getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(OrderBy orderBy) {
        this.orderBy = orderBy;
    }

    public boolean isAscending() {
        return ascending;
    }

    public void setAscending(boolean ascending) {
        this.ascending = ascending;
    }

    public boolean isOrphans() {
        return orphans;
    }

    public void setOrphans(boolean orphans) {
        this.orphans = orphans;
    }

    public void setCurrentDocument(Document currentDocument) {
        this.currentDocument = currentDocument;
    }

    public Document getCurrentDocument() {
        return currentDocument;
    }

    public void setFullText(String fullText) {
        this.fullText = fullText;
    }

    public String getFullText() {
        return fullText;
    }

    public TermLanguage getLanguage() {
        return language;
    }

    public void setLanguage(TermLanguage language) {
        this.language = language;
    }

    public void setIncludeAllDocs(boolean includeAllDocs) {
        this.includeAllDocs = includeAllDocs;
    }

    public boolean isIncludeAllDocs() {
        return includeAllDocs;
    }

    @Override
    public String toString() {
        return "DocumentNoteSearchInfo [noteTypes=" + noteTypes + ", noteFormats=" + noteFormats
                + ", creators=" + creators + ", language=" + language + ", orderBy=" + orderBy
                + ", ascending=" + ascending + ", orphans=" + orphans + ", includeAllDocs="
                + includeAllDocs + ", fullText=" + fullText + "]";
    }

}
