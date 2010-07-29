/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.pages.document;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.services.Response;

import com.mysema.tapestry.core.Context;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.pages.HttpError;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

/**
 * AbstractDocumentPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class AbstractDocumentPage {

    @Inject
    private DocumentRepository documentRepository;

    private Document document;

    private DocumentRevision documentRevision;

    @Property
    private List<RevisionInfo> revisions;

    @Property
    private RevisionInfo revision;

    private Context context;

    @Inject
    private Response response;

    void onActivate(EventContext ctx) throws IOException {
        context = new Context(ctx);
        if (ctx.getCount() == 0) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "No document ID given!");
        }
        try {
            document = documentRepository.getById(ctx.get(String.class, 0));
            revisions = documentRepository.getRevisions(document);
        } catch (RuntimeException e) {
            response.sendError(HttpError.PAGE_NOT_FOUND, "Document not found!");
            return;
        }
        long rev = -1;
        if (ctx.getCount() > 1){
            // TODO : block this for AnnotatePage
            try {
                rev = ctx.get(Long.class, 1);
            } catch (RuntimeException e) {
                response.sendError(HttpError.PAGE_NOT_FOUND, "Revision not numerical!");
            }
            if (!revisions.contains(new RevisionInfo(rev))) {
                response.sendError(HttpError.PAGE_NOT_FOUND, "Document revision not found!");
            }
        }else{
            // get latest
            rev = revisions.get(revisions.size() - 1).getSvnRevision();
        }
        Collections.reverse(revisions);
        documentRevision = new DocumentRevision(document, rev);
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
        return documentRepository;
    }


}
