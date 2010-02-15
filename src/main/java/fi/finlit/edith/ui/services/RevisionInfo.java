package fi.finlit.edith.ui.services;


/**
 * RevisionInfo provides
 *
 * @author tiwe
 * @version $Id$
 */
public class RevisionInfo {

    private final long svnRevision;
    
    private final String created;
    
    private final String creator;
    
    public RevisionInfo(long svnRevision, String created, String creator){
        this.svnRevision = svnRevision;
        this.created = created;
        this.creator = creator;
    }

    public long getSvnRevision() {
        return svnRevision;
    }

    public String getCreated() {
        return created;
    }

    public String getCreator() {
        return creator;
    }
    
    
    
}
