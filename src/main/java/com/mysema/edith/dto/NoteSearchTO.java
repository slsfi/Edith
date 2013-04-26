/*
 * Copyright (c) 2012 Mysema Ltd.
 * All rights reserved.
 *
 */
package com.mysema.edith.dto;

import java.util.HashSet;
import java.util.Set;

import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteStatus;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.TermLanguage;

public class NoteSearchTO {

    private Set<DocumentTO> documents = new HashSet<DocumentTO>();

    private Set<String> paths = new HashSet<String>();

    private NoteStatus noteStatus;
    
    private Set<NoteType> noteTypes = new HashSet<NoteType>();

    private Set<NoteFormat> noteFormats = new HashSet<NoteFormat>();

    private Set<UserTO> creators = new HashSet<UserTO>();

    private TermLanguage language;

    private String orderBy;

    private boolean ascending = true;

    private boolean orphans = false;

    private boolean includeAllDocs = false;

    private DocumentTO currentDocument;

    private String fullText;
    
    private String shortenedSelection, description, lemmaMeaning;
    
    private Long createdBefore, createdAfter, editedAfter, editedBefore; 
    
    private Long page, perPage;
    
    public NoteSearchTO() {
    }

    public NoteSearchTO(DocumentTO document) {
        currentDocument = document;
    }

    public Set<DocumentTO> getDocuments() {
        return documents;
    }

    public void setDocuments(Set<DocumentTO> documents) {
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

    public String getOrderBy() {
        return orderBy;
    }

    public void setOrderBy(String orderBy) {
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

    public void setCurrentDocument(DocumentTO currentDocument) {
        this.currentDocument = currentDocument;
    }

    public DocumentTO getCurrentDocument() {
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

    public NoteStatus getNoteStatus() {
        return noteStatus;
    }

    public void setNoteStatus(NoteStatus noteStatus) {
        this.noteStatus = noteStatus;
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
    
}
