/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.ui.services.AdminService;

/**
 * TestDataPage provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DummyDataPage {

    @Inject
    private NoteRepository noteRepository;
    
    @Inject
    private AdminService adminService;

    @Secured("ROLE_USER")
    void onActivate(){

    }
    
    public void onActionFromAddTestData() throws Exception{
        noteRepository.importNotes(new File("etc/demo-material/notes/nootit.xml"));
    }
    
    public void onActionFromRemoveNotes(){
        adminService.removeNotesAndTerms();
    }

}
