/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Mixin;
import com.mysema.rdfbean.annotations.Predicate;

@ClassMapping
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
    
    @Mixin
    private Concept concept = new Concept();

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
        copy.setFormat(getFormat());
        copy.setLemma(getLemma());
        copy.setLemmaMeaning(getLemmaMeaning());
        copy.setPerson(getPerson());
        copy.setPlace(getPlace());
        copy.setTerm(getTerm());
        copy.setConcept(concept.createCopy());
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

    public Concept getConcept(boolean extendedTerm) {
        if (extendedTerm) {
            return term.getConcept();
        } else {
            return concept;
        }
    }

    /**
     * Use the concept via getConcept(extendedTerm)
     * 
     * @return
     */
    @Deprecated
    public Concept getConcept() {
        return concept;
    }
    
    private void setConcept(Concept concept) {
        this.concept = concept;
    }
    
    public String getEditors(boolean extendedTerm) {
        Collection<String> result = new ArrayList<String>();
        for (UserInfo user : getConcept(extendedTerm).getAllEditors()) {
            if (!getConcept(extendedTerm).getLastEditedBy().equals(user)) {
                result.add(user.getUsername());
            }
        }
        return StringUtils.join(result, ", ");
    }
    

}
