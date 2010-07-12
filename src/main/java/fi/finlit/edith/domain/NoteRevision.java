/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.mysema.query.annotations.QueryInit;
import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.EDITH;

/**
 * NoteRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public class NoteRevision extends Identifiable {

    private static final Pattern WHITESPACE = Pattern.compile("\\s+");

    @Predicate
    private UserInfo createdBy;

    @Predicate
    private long createdOn;

    @Predicate
    private String description;

    @Predicate(ln = "latestRevision", inv = true)
    private Note latestRevisionOf;

    @Predicate
    private String lemma;

    @Predicate
    private String lemmaMeaning;

    @Predicate
    private NoteType type;

    @Predicate
    private String longText;

    @Predicate
    @QueryInit( { "*", "term.meaning" })
    private Note revisionOf;

    @Predicate
    private String subtextSources;

    @Predicate
    private long svnRevision;

    @Predicate
    private boolean deleted;

    @Predicate
    private NoteFormat format;

    @Predicate
    private Place place;

    @Predicate
    private Person person;

    @Predicate
    private String sources;

    // NOTE : not persisted
    private DocumentRevision docRevision;

    public NoteRevision createCopy() {
        NoteRevision copy = new NoteRevision();
        copy.setDescription(description);
        copy.setLemma(lemma);
        copy.setLemmaMeaning(lemmaMeaning);
        copy.setLongText(longText);
        copy.setRevisionOf(revisionOf);
        copy.setSVNRevision(svnRevision);
        copy.setSubtextSources(subtextSources);
        copy.setType(type);
        copy.setSources(sources);
        copy.setPerson(person);
        copy.setPlace(place);
        return copy;
    }

    public DocumentRevision getDocumentRevision() {
        if (docRevision == null || docRevision.getRevision() != svnRevision) {
            docRevision = getRevisionOf().getDocument().getRevision(svnRevision);
        }
        return docRevision;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

    public long getCreatedOn() {
        return createdOn;
    }

    public String getDescription() {
        return description;
    }

    public Note getLatestRevisionOf() {
        return latestRevisionOf;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLongText() {
        return longText;
    }

    public Note getRevisionOf() {
        return revisionOf;
    }

    public long getSvnRevision() {
        return svnRevision;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
    }

    public void setCreatedOn(long created) {
        createdOn = created;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

    public void setSVNRevision(long svnRevision) {
        this.svnRevision = svnRevision;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public String getLocalId() {
        return revisionOf.getLocalId();
    }

    public String getSubtextSources() {
        return subtextSources;
    }

    public void setSubtextSources(String subtextSources) {
        this.subtextSources = subtextSources;
    }

    public DateTime getCreatedOnDate() {
        return new DateTime(createdOn);
    }

    public String getLemmaMeaning() {
        return lemmaMeaning;
    }

    public void setLemmaMeaning(String lemmaMeaning) {
        this.lemmaMeaning = lemmaMeaning;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public NoteFormat getFormat() {
        return format;
    }

    public void setFormat(NoteFormat format) {
        this.format = format;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setPerson(Person person) {
        this.person = person;
    }

    public Person getPerson() {
        return person;
    }

    public void setSources(String sources) {
        this.sources = sources;
    }

    public String getSources() {
        return sources;
    }

    public void setLemmaFromLongText() {
        if (WHITESPACE.matcher(longText).find()) {
            String[] words = StringUtils.split(longText);
            if (words.length == 2) {
                lemma = words[0] + " " + words[1];
            } else if (words.length > 1) {
                lemma = words[0] + " \u2013 \u2013 " + words[words.length - 1];
            } else {
                lemma = words[0];
            }
        } else {
            lemma = longText;
        }
    }

    @Override
    public String toString() {
        return deleted ? lemma + " (deleted)" : lemma;
    }
}
