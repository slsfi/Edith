/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 * 
 */
package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;

/**
 * DocumentRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryTest extends AbstractServiceTest{
    
    @Inject
    private DocumentRepository documentRepo;
    
    @Inject 
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;
        
    @Test
    public void getAll(){
        assertEquals(7, documentRepo.getAll().size());
    }
    
    @Test
    public void getDocumentsOfFolder(){
        assertEquals(7, documentRepo.getDocumentsOfFolder(documentRoot).size()); 
    }
    
    @Test
    public void getDocumentForPath(){
        assertNotNull(documentRepo.getDocumentForPath("/documents/" + UUID.randomUUID().toString()));
    }
    
    @Test
    public void getDocumentFile() throws IOException{
        for (Document document : documentRepo.getAll()){
            File file = documentRepo.getDocumentFile(new DocumentRevision(document, -1));
            assertTrue(file.exists());
            assertTrue(file.isFile());
            assertTrue(file.length() > 0);
        }
    }
    
    @Test
    public void addDocument() throws IOException, SVNException{
        File file = File.createTempFile("test", null);
        FileUtils.writeStringToFile(file, "test file", "UTF-8");
        String targetPath = "/documents/" + UUID.randomUUID().toString();
        documentRepo.addDocument(targetPath, file);
        
        Document document = documentRepo.getDocumentForPath(targetPath);
        assertFalse(documentRepo.getRevisions(document).isEmpty());
        documentRepo.remove(document);
    }

    @Test
    public void getRevisions() throws SVNException{
        for (Document document : documentRepo.getAll()){
            assertFalse(documentRepo.getRevisions(document).isEmpty());
        }
    }
    
    @Override
    protected Class<?> getServiceClass() {
        return DocumentRepository.class;
    }
}
