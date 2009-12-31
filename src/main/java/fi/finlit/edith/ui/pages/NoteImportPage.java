package fi.finlit.edith.ui.pages;

import java.io.File;

import org.apache.tapestry5.annotations.Property;
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
public class NoteImportPage {
    
    @Inject
    private NoteRepository noteRepo;
    
    @Property
    private UploadedFile file;
    
    @Secured("ROLE_USER")
    void onActivate(){        
    }

    public void onSuccess() throws Exception{
        File tempFile = File.createTempFile("upload", null);
        try{
            file.write(tempFile);
            noteRepo.importNotes(tempFile);    
        }finally{
            tempFile.delete();    
        }        
    }

}
