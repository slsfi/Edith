/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages.document;

import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.InjectService;
import org.springframework.security.annotation.Secured;
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

    @InjectService("DocumentRepository")
    private DocumentRepository documentRepo;
       
    private Document document;
    
    private DocumentRevision documentRevision;
    
    @Property
    private List<Long> revisions;
    
    @Property
    private Long revision;
    
    private Context context;
    
    @Secured("ROLE_USER")
    void onActivate(EventContext context) throws SVNException{
        this.context = new Context(context);
        document = documentRepo.getById(context.get(String.class, 0));        
        revisions = documentRepo.getRevisions(document);
        long revision;
        if (context.getCount() > 1){
            // TODO : block this for AnnotatePage
            revision = context.get(Long.class, 1);
        }else{
            // get latest 
            revision = revisions.get(revisions.size() - 1);
        }
        documentRevision = new DocumentRevision(document, revision);
        
    }
    
    Object[] onPassivate(){
        return context.toArray();
    }

    public Document getDocument(){
        return document;
    }
    
    public DocumentRevision getDocumentRevision() {
        return documentRevision;
    }
    
    protected DocumentRepository getDocumentRepo(){
        return documentRepo;
    }
    
    
}
