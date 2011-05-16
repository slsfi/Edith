package fi.finlit.edith.domain;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;

import org.joda.time.DateTime;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

@ClassMapping
public class NoteComment extends Identifiable {

    @Predicate(ln="commentOf")
    private Concept concept;

    @Predicate
    private String message;

    @Predicate
    private String username;

    @Predicate
    private DateTime createdAt;

    public NoteComment() {
    }

    public NoteComment(Concept concept, String message, String username) {
        this.concept = concept;
        this.message = message;
        this.username = username;
        createdAt = new DateTime();
    }

    public Concept getConcept() {
        return concept;
    }

    public void setConcept(Concept concept) {
        this.concept = concept;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public DateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(DateTime createdAt) {
        this.createdAt = createdAt;
    }

    @Override
    public String toString() {
        return "Comment [message=" + message + "]";
    }

    public NoteComment copy() {
        NoteComment comment = new NoteComment();
        comment.setCreatedAt(createdAt);
        comment.setMessage(message);
        comment.setConcept(concept);
        comment.setUsername(username);
        return comment;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return super.equals(obj);
    }
    
    public static List<NoteComment> getSortedComments(Set<NoteComment> c) {
        List<NoteComment> rv = new ArrayList<NoteComment>(c);
        Collections.sort(rv, NoteCommentComparator.DESC);
        return rv;
    }

}
