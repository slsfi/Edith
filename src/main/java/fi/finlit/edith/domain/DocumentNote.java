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

import fi.finlit.edith.EDITH;

/**
 * NoteRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns = EDITH.NS)
public class DocumentNote extends Identifiable {
    @Predicate
    private Document document;

    /**
     * the id of the note in the context of the TEI document
     */
    @Predicate
    private String localId;

    @Predicate
    private String longText;

    @Predicate
    private long svnRevision;

    @Predicate
    private boolean deleted;

    @Predicate
    private UserInfo createdBy;

    @Predicate
    private long createdOn;

    @Predicate
    private NoteStatus status = NoteStatus.INITIAL;

    // NOTE : not persisted
    private DocumentRevision docRevision;

    @Predicate
    private Note note;

    public DocumentNote createCopy() {
        DocumentNote copy = new DocumentNote();
        copy.setLongText(longText);
        copy.setDocument(document);
        copy.setNote(note);
        copy.setDocument(document);
        copy.setStatus(status);
        copy.setDocRevision(docRevision);
        copy.setCreatedBy(createdBy);
        copy.setCreatedOn(createdOn);
        copy.setSVNRevision(svnRevision);
        copy.setDeleted(deleted);
        copy.setLocalId(localId);
        return copy;
    }

    public UserInfo getCreatedBy() {
        return createdBy;
    }

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

    public String getLocalId() {
        return localId;
    }

    public String getLongText() {
        return longText;
    }

    public Note getNote() {
        return note;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public long getSVNRevision() {
        return svnRevision;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setCreatedBy(UserInfo createdBy) {
        this.createdBy = createdBy;
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

    public void setLocalId(String localId) {
        this.localId = localId;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setNote(Note note) {
        this.note = note;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setSVNRevision(long svnRevision) {
        this.svnRevision = svnRevision;
    }

    @Override
    public String toString() {
        return "DocumentNote: " + StringUtils.abbreviate(longText, 30);
    }

}
