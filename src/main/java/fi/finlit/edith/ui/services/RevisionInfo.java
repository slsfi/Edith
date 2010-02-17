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

    public RevisionInfo(long svnRevision){
        this.svnRevision = svnRevision;
        this.created = "";
        this.creator = "";
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

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + (int) (svnRevision ^ (svnRevision >>> 32));
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        RevisionInfo other = (RevisionInfo) obj;
        if (svnRevision != other.svnRevision) {
            return false;
        }
        return true;
    }
}
