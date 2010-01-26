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
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.tmatesoft.svn.core.SVNException;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentRepository;
import fi.finlit.edith.domain.DocumentRevision;
import fi.finlit.edith.ui.services.SubversionService;

/**
 * DocumentRepositoryTest provides
 *
 * @author tiwe
 * @version $Id$
 */
public class DocumentRepositoryTest extends AbstractServiceTest {

    @Inject
    private DocumentRepository documentRepo;
    
    @Inject
    private SubversionService subversionService;
    
    private static boolean initialized = false;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    private List<Document> savedDocs = new ArrayList<Document>();

    @Before
    public void setUp(){
        if (!initialized){
            subversionService.destroy();
            subversionService.initialize();
            initialized = true;
        }        
    }
    
    @After
    public void tearDown(){
        for (Document doc : savedDocs){
            documentRepo.remove(doc);
        }
    }

    @Test
    public void getAll() {
        assertEquals(7, documentRepo.getAll().size());
    }

    @Test
    public void getDocumentsOfFolder() {
        assertEquals(7, documentRepo.getDocumentsOfFolder(documentRoot).size());
    }

    @Test
    public void getDocumentForPath() {
        assertNotNull(documentRepo.getDocumentForPath("/documents/"
                + UUID.randomUUID().toString()));
    }

    @Test
    public void getDocumentFile() throws IOException {
        for (Document document : documentRepo.getAll()) {
            File file = documentRepo.getDocumentFile(new DocumentRevision(
                    document, -1));
            assertTrue(file.exists());
            assertTrue(file.isFile());
            assertTrue(file.length() > 0);
        }
    }

    @Test
    public void addDocument() throws IOException, SVNException {
        File file = File.createTempFile("test", null);
        FileUtils.writeStringToFile(file, "test file", "UTF-8");
        String targetPath = "/documents/" + UUID.randomUUID().toString();
        documentRepo.addDocument(targetPath, file);

        Document document = documentRepo.getDocumentForPath(targetPath);
        savedDocs.add(document);
        assertFalse(documentRepo.getRevisions(document).isEmpty());
    }

    @Test
    public void getRevisions() throws SVNException {
        for (Document document : documentRepo.getAll()) {
            assertFalse(documentRepo.getRevisions(document).isEmpty());
        }
    }

    @Test
    public void addNote() {
        // TODO
    }

    @Override
    protected Class<?> getServiceClass() {
        return DocumentRepository.class;
    }
}
