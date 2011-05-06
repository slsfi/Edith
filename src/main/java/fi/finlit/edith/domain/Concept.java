package fi.finlit.edith.domain;

import java.util.HashSet;
import java.util.Set;

import com.mysema.rdfbean.annotations.ClassMapping;
import com.mysema.rdfbean.annotations.Predicate;

@ClassMapping
public abstract class Concept extends Identifiable {

    @Predicate
    private Set<UserInfo> allEditors = new HashSet<UserInfo>();

    @Predicate(ln = "commentOf", inv = true)
    private Set<NoteComment> comments = new HashSet<NoteComment>();

    @Predicate
    private UserInfo lastEditedBy;

    @Predicate(ln="sourcesString")
    private String sources;

    @Predicate(ln="subtextSourcesString")
    private String subtextSources;
    
    @Predicate(ln="descriptionString")
    private String description;

    @Predicate
    private NoteStatus status = NoteStatus.INITIAL;

    @Predicate
    private Set<NoteType> types = new HashSet<NoteType>();
    
    public Set<UserInfo> getAllEditors() {
        return allEditors;
    }
    
    public Set<NoteComment> getComments() {
        return comments;
    }

    public String getDescription() {
        return description;
    }

    public UserInfo getLastEditedBy() {
        return lastEditedBy;
    }

    public String getSources() {
        return sources;
    }

    public NoteStatus getStatus() {
        return status;
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
   
    public void setDescription(String descriptionString) {
        this.description = descriptionString;
    }

    public void setLastEditedBy(UserInfo lastEditedBy) {
        this.lastEditedBy = lastEditedBy;
    }

    public void setSources(String sourcesString) {
        this.sources = sourcesString;
    }
    
    public void setStatus(NoteStatus status) {
        this.status = status;
    }

    public void setTypes(Set<NoteType> types) {
        this.types = types;
    }
    
    public String getSubtextSources() {
        return subtextSources;
    }

    public void setSubtextSources(String subtextSourcesString) {
        this.subtextSources = subtextSourcesString;
    }
    
}
