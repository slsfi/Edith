/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.NoteRepository;

/**
 * NoteRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class NoteRepositoryTest extends AbstractServiceTest{

    @Inject
    private NoteRepository noteRepo;
    
    @Test
    public void importNotes() throws Exception{
        assertEquals(133, noteRepo.importNotes(new File("etc/demo-material/notes/nootit.xml")));        
        assertEquals(1l, noteRepo.queryNotes("lemma").getAvailableRows());
        assertEquals(2l, noteRepo.queryNotes("etten anna sinulle").getAvailableRows());
    }
}
