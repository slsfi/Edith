/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import fi.finlit.edith.domain.DocumentRepository;

/**
 * DocumentsPage provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentsPage {
    
    @Inject
    private DocumentRepository documentRepo;
    
    @Secured("ROLE_USER")
    void onActivate(){
        
    }

}
