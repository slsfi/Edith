/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;

/**
 * DocumentRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryTest extends AbstractServiceTest{
    
    @Inject
    private DocumentRepository documentRepo;
        
    @Test
    public void getAll(){
        assertEquals(6, documentRepo.getAll().size());
    }
    
    @Test
    public void getDocumentsOfFolder(){
        assertEquals(6, documentRepo.getDocumentsOfFolder("documents/trunk").size()); 
    }
    
    @Test
    public void getDocumentForPath(){
        assertNotNull(documentRepo.getDocumentForPath("/documents/" + UUID.randomUUID().toString()));
    }
    
    @Test
    public void getDocumentFile() throws IOException{
        for (Document document : documentRepo.getAll()){
            File file = documentRepo.getDocumentFile(document.getSvnPath(), -1);
            assertTrue(file.exists());
            assertTrue(file.isFile());
            assertTrue(file.length() > 0);
        }
    }
}
