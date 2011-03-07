/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

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

    @Predicate(ln="descriptionString")
    private String description;

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

    @Predicate(ln="sourcesString")
    private String sources;

    @Predicate
    private NoteStatus status = NoteStatus.INITIAL;

    @Predicate(ln="subtextSourcesString")
    private String subtextSources;

    @Predicate
    private Term term;

    @Predicate
    private Set<NoteType> types;

    @Predicate
    private long editedOn;

    public Note createCopy(){
        Note copy = new Note();
        Set<NoteComment> commentsCopy = new HashSet<NoteComment>();
        for (NoteComment comment : getComments()) {
            NoteComment copyOfComment = comment.copy();
            copyOfComment.setNote(copy);
            commentsCopy.add(copyOfComment);
            // getSession().save(copyOfComment);
        }
        copy.setComments(commentsCopy);
        copy.setDescription(getDescription());
        copy.setFormat(getFormat());
        copy.setLemma(getLemma());
        copy.setLemmaMeaning(getLemmaMeaning());
        copy.setPerson(getPerson());
        copy.setPlace(getPlace());
        copy.setSources(getSources());
        copy.setSubtextSources(getSubtextSources());
        copy.setTerm(getTerm());
        copy.setTypes(getTypes());
        return copy;
    }

    public Set<UserInfo> getAllEditors() {
        return allEditors;
    }

    public Set<NoteComment> getComments() {
        return comments;
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

    public NoteStatus getStatus() {
        return status;
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

    public void setStatus(NoteStatus status) {
        this.status = status;
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

    @Override
    public String toString() {
        return "Note [lemma=" + lemma + "]";
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String descriptionString) {
        this.description = descriptionString;
    }

    public String getSources() {
        return sources;
    }

    public void setSources(String sourcesString) {
        this.sources = sourcesString;
    }

    public String getSubtextSources() {
        return subtextSources;
    }

    public void setSubtextSources(String subtextSourcesString) {
        this.subtextSources = subtextSourcesString;
    }

}
