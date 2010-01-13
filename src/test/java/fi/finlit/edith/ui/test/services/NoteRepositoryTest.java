/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Before;
import org.junit.Test;

import fi.finlit.edith.domain.NoteRepository;
import fi.finlit.edith.domain.NoteRevisionRepository;

/**
 * NoteRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryTest extends AbstractServiceTest{

    @Inject
    private NoteRepository noteRepo;
    
    @Inject
    private NoteRevisionRepository noteRevisionRepo;
    
    @Test
    public void importNotes() throws Exception{
        assertEquals(133, noteRepo.importNotes(new File("etc/demo-material/notes/nootit.xml")));        
        assertEquals(1l, noteRevisionRepo.queryNotes("lemma").getAvailableRows());
        assertEquals(2l, noteRevisionRepo.queryNotes("etten anna sinulle").getAvailableRows());
    }
}
