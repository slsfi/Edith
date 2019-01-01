/*
 * Copyright (c) 2018 Mysema
 */

package com.mysema.edith.dto;

import java.util.Set;

import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteStatus;
import com.mysema.edith.domain.NoteType;

public class NoteTO {

    private Long id;

    private String lemma;

    private TermTO term;

    private Long editedOn;

    private UserTO lastEditedBy;

    private String sources;

    private String subtextSources;

    private boolean deleted;

    private String description;

    private Set<NoteType> types;

    private NoteFormat format;

    private String lemmaMeaning;

    private PersonTO person;

    private PlaceTO place;

    private Set<UserTO> allEditors;

    private NoteStatus status;

    private NoteCommentTO comment;

    private int documentNoteCount;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getLemma() {
        return lemma;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public TermTO getTerm() {
        return term;
    }

    public void setTerm(TermTO term) {
        this.term = term;
    }

    public Long getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(Long editedOn) {
        this.editedOn = editedOn;
    }

    public UserTO getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(UserTO lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getSubtextSources() {
        return subtextSources;
    }

    public void setSubtextSources(String subtextSources) {
        this.subtextSources = subtextSources;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Set<NoteType> getTypes() {
        return types;
    }

    public void setTypes(Set<NoteType> types) {
        this.types = types;
    }

    public NoteFormat getFormat() {
        return format;
    }

    public void setFormat(NoteFormat format) {
        this.format = format;
    }

    public String getLemmaMeaning() {
        return lemmaMeaning;
    }

    public void setLemmaMeaning(String lemmaMeaning) {
        this.lemmaMeaning = lemmaMeaning;
    }

    public PersonTO getPerson() {
        return person;
    }

    public void setPerson(PersonTO person) {
        this.person = person;
    }

    public PlaceTO getPlace() {
        return place;
    }

    public void setPlace(PlaceTO place) {
        this.place = place;
    }

    public Set<UserTO> getAllEditors() {
        return allEditors;
    }

    public void setAllEditors(Set<UserTO> allEditors) {
        this.allEditors = allEditors;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public NoteCommentTO getComment() {
        return comment;
    }

    public void setComment(NoteCommentTO comment) {
        this.comment = comment;
    }

    public int getDocumentNoteCount() {
        return documentNoteCount;
    }

    public void setDocumentNoteCount(int documentNoteCount) {
        this.documentNoteCount = documentNoteCount;
    }
}
