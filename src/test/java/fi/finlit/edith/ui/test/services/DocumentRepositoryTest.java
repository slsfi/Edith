package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import java.util.UUID;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Test;

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
    
}
