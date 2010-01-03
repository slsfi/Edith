/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.pages;

import java.io.File;

import org.apache.tapestry5.PersistenceConstants;
import org.apache.tapestry5.annotations.Persist;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.upload.services.UploadedFile;
import org.springframework.security.annotation.Secured;

import fi.finlit.edith.domain.NoteRepository;

/**
 * NoteImportPage provides
 *
 * @author tiwe
 * @version $Id$
 */
@SuppressWarnings("unused")
public class NoteImportPage {
    
    @Inject
    private NoteRepository noteRepo;
    
    @Property
    private UploadedFile file;
    
    @Inject
    private Messages messages;
    
    @Persist(PersistenceConstants.FLASH)
    @Property
    private String message;
    
    @Secured("ROLE_USER")
    void onActivate(){        
    }

    public void onSuccess() throws Exception{
        File tempFile = File.createTempFile("upload", null);
        try{
            file.write(tempFile);
            int rv = noteRepo.importNotes(tempFile);
            message = messages.format("notes-imported-msg", rv);
        }finally{
            tempFile.delete();    
        }        
    }

}
