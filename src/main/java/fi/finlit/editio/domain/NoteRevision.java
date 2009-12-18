package fi.finlit.editio.domain;

import java.util.Set;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

import fi.finlit.editio.EDITIO;

/**
 * Note provides
 *
 * @author tiwe
 * @version $Id$
 */
@ClassMapping(ns=EDITIO.NS)
public class NoteRevision {
    
//    TODO  : subtekstinLahde, sitaatti, lahde;
    
    @Predicate(ln="validFor")
    private Set<Long> svnRevisions;
    
    @Predicate
    private String basicForm; // perusmuoto
    
    @Predicate
    private DateTime created;
    
    @Predicate
    private User createdBy;
    
    @Predicate
    private String explanation; // selitys 

    @Predicate
    private Note revisionOf;
    
    @Predicate
    private NoteStatus status;
    
    @Predicate
    private String lemma;

    @Predicate
    private String longText; // pitkä viite

    @Predicate
    private String meaning; // merkitys

    @Predicate(ln="tagged")
    private Set<Tag> tags;

    public String getBasicForm() {
        return basicForm;
    }

    public DateTime getCreated() {
        return created;
    }

    public User getCreatedBy() {
        return createdBy;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getLemma() {
        return lemma;
    }

    public String getLongText() {
        return longText;
    }
    
    public String getMeaning() {
        return meaning;
    }
    
    public Set<Tag> getTags() {
        return tags;
    }
    
    public void setBasicForm(String basicForm) {
        this.basicForm = basicForm;
    }

    public void setCreated(DateTime created) {
        this.created = created;
    }

    public void setCreatedBy(User createdBy) {
        this.createdBy = createdBy;
    }

    public void setExplanation(String explanation) {
        this.explanation = explanation;
    }

    public void setLemma(String lemma) {
        this.lemma = lemma;
    }

    public void setLongText(String longText) {
        this.longText = longText;
    }

    public void setMeaning(String meaning) {
        this.meaning = meaning;
    }

    public void setTags(Set<Tag> tags) {
        this.tags = tags;
    }

    public Set<Long> getSvnRevisions() {
        return svnRevisions;
    }

    public void setSvnRevisions(Set<Long> svnRevisions) {
        this.svnRevisions = svnRevisions;
    }

    public Note getRevisionOf() {
        return revisionOf;
    }

    public void setRevisionOf(Note revisionOf) {
        this.revisionOf = revisionOf;
    }

    public NoteStatus getStatus() {
        return status;
    }

    public void setStatus(NoteStatus status) {
        this.status = status;
    }
 
    
}
