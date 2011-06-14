/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.domain;

import org.apache.commons.lang.StringUtils;
import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.edith.dto.DocumentRevision;

@ClassMapping
public class DocumentNote extends Identifiable {

    @Predicate
    private Document document;

    @Predicate
    private String longText;

    @Predicate
    private String shortText;

    @Predicate
    private Long svnRevision;

    @Predicate
    private boolean deleted;

    @Predicate
    private long createdOn;

    // NOTE : not persisted
    private DocumentRevision docRevision;

    @Predicate
    private Note note;

    @Predicate
    private boolean publishable;

    @Predicate
    private String lemmaPosition;

    @Predicate
    private int position;

    public long getCreatedOn() {
        return createdOn;
    }

    public DateTime getCreatedOnDate() {
        return new DateTime(createdOn);
    }

    public DocumentRevision getDocRevision() {
        return docRevision;
    }

    public Document getDocument() {
        return document;
    }

    public DocumentRevision getDocumentRevision() {
        if (docRevision == null || docRevision.getRevision() != svnRevision) {
            docRevision = document.getRevision(svnRevision);
        }
        return docRevision;
    }

    public String getLongText() {
        return longText;
    }

    public void setShortText(String shortText) {
        this.shortText = shortText;
    }

    public String getShortText() {
        return shortText;
    }

    public Note getNote() {
        return note;
    }

    public Long getSVNRevision() {
        return svnRevision;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setCreatedOn(long created) {
        createdOn = created;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }

    public void setDocRevision(DocumentRevision docRevision) {
        this.docRevision = docRevision;
    }

    public void setDocument(Document document) {
        this.document = document;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setSVNRevision(Long svnRevision) {
        this.svnRevision = svnRevision;
    }

    public void setPublishable(boolean publishable) {
        this.publishable = publishable;
    }

    public boolean isPublishable() {
        return publishable;
    }

    public String getLemmaPosition() {
        return lemmaPosition;
    }

    public void setLemmaPosition(String lemmaPosition) {
        this.lemmaPosition = lemmaPosition;
    }

    @Override
    public String toString() {
        return "DocumentNote: " + StringUtils.abbreviate(longText, 30);
    }

    public Concept getConcept(boolean extendedTerm) {
        return note.getConcept(extendedTerm);
    }

    public void setPosition(int position) {
        this.position = position;
    }

    public int getPosition() {
        return position;
    }

}
