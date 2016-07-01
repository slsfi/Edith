/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteStatus;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.Filters;
import com.mysema.edith.domain.TermLanguage;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NoteSearchTO {

    private Set<Long> documents = new HashSet<Long>();

    private Set<String> paths = new HashSet<String>();

    private NoteStatus status;

    private Set<NoteType> types = new HashSet<NoteType>();

    private Set<Filters> filters = new HashSet<Filters>();

    private Set<NoteFormat> formats = new HashSet<NoteFormat>();

    private Set<Long> creators = new HashSet<Long>();

    private TermLanguage language;

    private String order;

    private boolean ascending = true;

    private boolean orphans = false;

    private boolean includeAllDocs = false;

    private DocumentTO currentDocument;

    private String query;

    private String shortenedSelection, description, lemma, lemmaMeaning;

    private Long createdBefore, createdAfter, editedAfter, editedBefore;

    private Long page = 1l, perPage = 25l;

    private List<String> columns;

    public NoteSearchTO() {
    }

    public NoteSearchTO(DocumentTO document) {
        currentDocument = document;
    }

    public Set<Long> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<Long> documents) {
        this.documents = documents;
    }

    public void setPaths(Set<String> paths) {
        this.paths = paths;
    }

    public Set<String> getPaths() {
        return paths;
    }

    public Set<NoteType> getTypes() {
        return types;
    }

    public void setTypes(Set<NoteType> types) {
        this.types = types;
    }

    public Set<Filters> getFilters() { return filters; }

    public void setFilters(Set<Filters> filters) {this.filters = filters;}


    public Set<NoteFormat> getFormats() {
        return formats;
    }

    public void setFormats(Set<NoteFormat> formats) {
        this.formats = formats;
    }

    public Set<Long> getCreators() {
        return creators;
    }

    public void setCreators(Set<Long> creators) {
        this.creators = creators;
    }

    public String getOrder() {
        return order;
    }

    public void setOrder(String order) {
        this.order = order;
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

    public void setCurrentDocument(DocumentTO currentDocument) {
        this.currentDocument = currentDocument;
    }

    public DocumentTO getCurrentDocument() {
        return currentDocument;
    }

    public void setQuery(String query) {
        this.query = query;
    }

    public String getQuery() {
        return query;
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

    public Long getPage() {
        return page;
    }

    public void setPage(Long page) {
        this.page = page;
    }

    public Long getPerPage() {
        return perPage;
    }

    public void setPerPage(Long perPage) {
        this.perPage = perPage;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLemmaMeaning() {
        return lemmaMeaning;
    }

    public void setLemmaMeaning(String lemmaMeaning) {
        this.lemmaMeaning = lemmaMeaning;
    }

    public String getShortenedSelection() {
        return shortenedSelection;
    }

    public void setShortenedSelection(String shortenedSelection) {
        this.shortenedSelection = shortenedSelection;
    }

    public Long getCreatedBefore() {
        return createdBefore;
    }

    public void setCreatedBefore(Long createdBefore) {
        this.createdBefore = createdBefore;
    }

    public Long getCreatedAfter() {
        return createdAfter;
    }

    public void setCreatedAfter(Long createdAfter) {
        this.createdAfter = createdAfter;
    }

    public Long getEditedAfter() {
        return editedAfter;
    }

    public void setEditedAfter(Long editedAfter) {
        this.editedAfter = editedAfter;
    }

    public Long getEditedBefore() {
        return editedBefore;
    }

    public void setEditedBefore(Long editedBefore) {
        this.editedBefore = editedBefore;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public List<String> getColumns() {
        return columns;
    }

    public void setColumns(List<String> columns) {
        this.columns = columns;
    }

    public String getDirection() {
        return ascending ? "ASC" : "DESC";
    }

    public void setDirection(String direction) {
        ascending = direction == null || direction.equals("ASC");
    }

}
