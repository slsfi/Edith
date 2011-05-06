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

@ClassMapping
public class Note extends Concept {

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
    private NoteFormat format;

    @Predicate
    private String lemma;

    @Predicate
    private String lemmaMeaning;

    @Predicate
    private Person person;

    @Predicate
    private Place place;

    @Predicate
    private Term term;

    @Predicate
    private long editedOn;

    public Note createCopy(){
        Note copy = new Note();
        Set<NoteComment> commentsCopy = new HashSet<NoteComment>();
        for (NoteComment comment : getComments()) {
            NoteComment copyOfComment = comment.copy();
            copyOfComment.setConcept(copy);
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


    public NoteFormat getFormat() {
        return format;
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

    public Term getTerm() {
        return term;
    }

    public void setFormat(NoteFormat format) {
        this.format = format;
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


    public void setTerm(Term term) {
        this.term = term;
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



}
