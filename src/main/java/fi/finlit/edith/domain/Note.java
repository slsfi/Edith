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

    // TODO : refactor to String
    @Predicate(ln = "desc")
    public Paragraph description;

    @Predicate(ln="descriptionString")
    private String descriptionString;

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

    // TODO : refactor to String
    @Predicate
    public Paragraph sources;

    @Predicate(ln="sourcesString")
    private String sourcesString;

    @Predicate
    private NoteStatus status = NoteStatus.INITIAL;

    // TODO : refactor to String
    @Predicate
    public Paragraph subtextSources;

    @Predicate(ln="subtextSourcesString")
    private String subtextSourcesString;

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
        copy.setDescriptionString(getDescriptionString());
//        if (getDescription() != null) {
//            copy.setDescription(getDescription().copy());
//        }
        copy.setFormat(getFormat());
        copy.setLemma(getLemma());
        copy.setLemmaMeaning(getLemmaMeaning());
        copy.setPerson(getPerson());
        copy.setPlace(getPlace());
        copy.setSourcesString(getSourcesString());
//        if (getSources() != null) {
//            copy.setSources(getSources().copy());
//        }
//        copy.setSubtextSources(getSubtextSources());
        copy.setSubtextSourcesString(getSubtextSourcesString());
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

    public Paragraph getDescription(){
        return Paragraph.parseSafe(descriptionString);
    }

    public String getDescriptionString() {
        return descriptionString;
    }

    public void setDescriptionString(String descriptionString) {
        this.descriptionString = descriptionString;
    }

    public Paragraph getSources(){
        return Paragraph.parseSafe(sourcesString);
    }

    public String getSourcesString() {
        return sourcesString;
    }

    public void setSourcesString(String sourcesString) {
        this.sourcesString = sourcesString;
    }

    public Paragraph getSubtextSources(){
        return Paragraph.parseSafe(subtextSourcesString);
    }

    public String getSubtextSourcesString() {
        return subtextSourcesString;
    }

    public void setSubtextSourcesString(String subtextSourcesString) {
        this.subtextSourcesString = subtextSourcesString;
    }

}
