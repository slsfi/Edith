package fi.finlit.edith.ui.pages;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.springframework.security.annotation.Secured;

import fi.finlit.edith.domain.NoteRepository;

/**
 * TestDataPage provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DummyDataPage {

    @Inject
    private NoteRepository noteRepository;

    @Secured("ROLE_USER")
    void onActivate(){

    }
    
    public void onActionFromAddTestData() throws Exception{
        noteRepository.importNotes(new File("etc/demo-material/notes/nootit.xml"));
    }

}
