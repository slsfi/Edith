package fi.finlit.edith.ui.pages.document;

import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.tmatesoft.svn.core.SVNException;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * AbstractDocumentPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class AbstractDocumentPage {

    @Inject
    private DocumentRepository documentRepo;
       
    @Property
    private Document document;
    
    @Property
    private DocumentRevision documentRevision;
    
    @Property
    private List<Long> revisions;
    
    @Property
    private Long revision;
    
    private Context context;
    
    void onActivate(EventContext context) throws SVNException{
        this.context = new Context(context);
        document = documentRepo.getById(context.get(String.class, 0));
        long revision = -1;
        if (context.getCount() > 1){
            revision = context.get(Long.class, 1);
        }
        documentRevision = new DocumentRevision(document, revision);
        revisions = documentRepo.getRevisions(document);            
    }
    
    Object[] onPassivate(){
        return context.toArray();
    }
}
