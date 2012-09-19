package com.mysema.edith.dto;

import java.util.Set;

import com.mysema.edith.domain.NoteFormat;
import com.mysema.edith.domain.NoteType;

public class NoteInfo {
    
    private Long id;
    
    private String lemma;
    
    private TermInfo term;
    
    private Long editedOn;
    
    private UserInfo lastEditedBy;
    
    private String sources;
    
    private String subtextSources;
    
    private boolean deleted;
    
    private String description;
    
    private Set<NoteType> types;
    
    private NoteFormat format;
    
    private String lemmaMeaning;
    
    private PersonInfo person;
    
    private PlaceInfo place;

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

    public TermInfo getTerm() {
        return term;
    }

    public void setTerm(TermInfo term) {
        this.term = term;
    }

    public Long getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(Long editedOn) {
        this.editedOn = editedOn;
    }

    public UserInfo getLastEditedBy() {
        return lastEditedBy;
    }

    public void setLastEditedBy(UserInfo lastEditedBy) {
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

    public PersonInfo getPerson() {
        return person;
    }

    public void setPerson(PersonInfo person) {
        this.person = person;
    }

    public PlaceInfo getPlace() {
        return place;
    }

    public void setPlace(PlaceInfo place) {
        this.place = place;
    }
    
    

}
