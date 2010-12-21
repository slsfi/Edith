/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public class Note extends Identifiable {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    public static String createLemmaFromLongText(String text) {
        String result = null;
        if (WHITESPACE.matcher(text).find()) {
            String[] words = StringUtils.split(text);
            if (words.length == 2) {
                result = words[0] + " " + words[1];
            } else if (words.length > 1) {
                result = words[0] + " \u2013 \u2013 " + words[words.length - 1];
            } else {
                result = words[0];
            }
        } else {
            result = text;
        }
        return result;
    }

    @Predicate
    private Set<UserInfo> allEditors;

    @Predicate(ln = "commentOf", inv = true)
    private Set<NoteComment> comments;

    @Predicate(ln = "desc")
    private Paragraph description;

    @Predicate
    private NoteFormat format;

    @Predicate
    private UserInfo lastEditedBy;

    @Predicate
    private String lemma;

    @Predicate
    private String lemmaMeaning;

    @Predicate
    private Person person;

    @Predicate
    private Place place;

    @Predicate
    private Paragraph sources;

    @Predicate
    private NoteStatus status = NoteStatus.INITIAL;

    @Predicate
    private Paragraph subtextSources;

    @Predicate
    private Term term;

    @Predicate
    private Set<NoteType> types;

    @Predicate
    private long editedOn;
    
    @Predicate(ln="concept")
    private Set<OntologyConcept> concepts;

    public Set<UserInfo> getAllEditors() {
        return allEditors;
    }

    public Set<NoteComment> getComments() {
        return comments;
    }

    public Paragraph getDescription() {
        return description;
    }

    public NoteFormat getFormat() {
        return format;
    }

    public UserInfo getLastEditedBy() {
        return lastEditedBy;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLemmaMeaning() {
        return lemmaMeaning;
    }

    public Person getPerson() {
        return person;
    }

    public Place getPlace() {
        return place;
    }

    public Paragraph getSources() {
        return sources;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public Paragraph getSubtextSources() {
        return subtextSources;
    }

    public Term getTerm() {
        return term;
    }

    public Set<NoteType> getTypes() {
        return types;
    }

    public void setAllEditors(Set<UserInfo> allEditors) {
        this.allEditors = allEditors;
    }

    public void setComments(Set<NoteComment> comments) {
        this.comments = comments;
    }

    public void setDescription(Paragraph description) {
        this.description = description;
    }

    public void setFormat(NoteFormat format) {
        this.format = format;
    }

    public void setLastEditedBy(UserInfo lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setLemmaMeaning(String lemmaMeaning) {
        this.lemmaMeaning = lemmaMeaning;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setSources(Paragraph sources) {
        this.sources = sources;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setSubtextSources(Paragraph subtextSources) {
        this.subtextSources = subtextSources;
    }

    public void setTerm(Term term) {
        this.term = term;
    }

    public void setTypes(Set<NoteType> types) {
        this.types = types;
    }

    public long getEditedOn() {
        return editedOn;
    }

    public void setEditedOn(long editedOn) {
        this.editedOn = editedOn;
    }

    public DateTime getEditedOnDate() {
        return new DateTime(editedOn);
    }
    
    public Set<OntologyConcept> getConcepts() {
        return concepts;
    }

    public void setConcepts(Set<OntologyConcept> concepts) {
        this.concepts = concepts;
    }

    @Override
    public String toString() {
        return "Note [lemma=" + lemma + "]";
    }

}
