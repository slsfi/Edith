package fi.finlit.edith.domain;

/**
 * DocumentRevision provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRevision {
    
    private final String svnPath;
    
    private final long revision;
    
    public DocumentRevision(Document document, long revision){
        this(document.getSvnPath(), revision);
    }

    public DocumentRevision(String svnPath, long revision){
        this.svnPath = svnPath;
        this.revision = revision;
    }
    
    public long getRevision() {
        return revision;
    }

    public String getSvnPath() {
        return svnPath;
    }

}
