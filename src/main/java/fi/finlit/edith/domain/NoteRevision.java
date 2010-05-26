/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.joda.time.LocalDate;

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
    private NoteFormat format;

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
    private NoteType type;

    @Predicate
    private NameForms place;

    @Predicate
    private NameForms person;

    @Predicate
    private LocalDate timeOfBirth;

    @Predicate
    private LocalDate timeOfDeath;

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
        copy.setFormat(format);
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

    public LocalDate getCreatedOnDate() {
        return new LocalDate(createdOn);
    }

    public String getLemmaMeaning() {
        return lemmaMeaning;
    }

    public void setLemmaMeaning(String lemmaMeaning) {
        this.lemmaMeaning = lemmaMeaning;
    }

    public NoteFormat getFormat() {
        return format;
    }

    public void setFormat(NoteFormat format) {
        this.format = format;
    }

    public NoteType getType() {
        return type;
    }

    public void setType(NoteType type) {
        this.type = type;
    }

    public NameForms getPlace() {
        return place;
    }

    public void setPlace(NameForms place) {
        this.place = place;
    }

    public void setPerson(NameForms person) {
        this.person = person;
    }

    public NameForms getPerson() {
        return person;
    }

    public void setTimeOfDeath(LocalDate timeOfDeath) {
        this.timeOfDeath = timeOfDeath;
    }

    public void setTimeOfBirth(LocalDate timeOfBirth) {
        this.timeOfBirth = timeOfBirth;
    }

    public LocalDate getTimeOfDeath() {
        return timeOfDeath;
    }

    public LocalDate getTimeOfBirth() {
        return timeOfBirth;
    }

    public void setLemmaFromLongText() {
        if (WHITESPACE.matcher(longText).find()) {
            String[] words = StringUtils.split(longText);
            if (words.length > 1) {
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
