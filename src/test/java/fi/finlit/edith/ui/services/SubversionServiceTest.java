/*
 * Copyright (c) 2009 Mysema Ltd.
 * All rights reserved.
 *
 */
package fi.finlit.edith.ui.services;

import static org.easymock.EasyMock.eq;
import static org.easymock.EasyMock.expect;
import static org.easymock.EasyMock.isA;
import static org.easymock.EasyMock.isNull;
import static org.easymock.classextension.EasyMock.createMock;
import static org.easymock.classextension.EasyMock.replay;
import static org.easymock.classextension.EasyMock.verify;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Autobuild;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.io.SVNRepository;
import org.tmatesoft.svn.core.wc.SVNClientManager;
import org.tmatesoft.svn.core.wc.SVNCommitClient;
import org.tmatesoft.svn.core.wc.SVNRevision;
import org.tmatesoft.svn.core.wc.SVNUpdateClient;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.EdithTestConstants;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.hibernate.AbstractHibernateTest;
import fi.finlit.edith.ui.services.svn.FileItem;
import fi.finlit.edith.ui.services.svn.SubversionException;
import fi.finlit.edith.ui.services.svn.SubversionServiceImpl;
import fi.finlit.edith.ui.services.svn.UpdateCallback;

public class SubversionServiceTest extends AbstractHibernateTest {

    private static final Logger logger = LoggerFactory.getLogger(SubversionServiceTest.class);

    @Autobuild
    @Inject
    private SubversionServiceImpl subversionService;

    @Inject
    private AuthService authService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject
    @Symbol(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject
    @Symbol(EDITH.REPO_FILE_PROPERTY)
    private File svnRepo;

    private File checkoutDirectory;

    private File anotherCheckoutDirectory;

    private File testFile;

    private final File svnRepoCopy = new File("target/repoCopy");

    @Before
    public void setUp() throws Exception {
        subversionService.initialize();
        checkoutDirectory = new File("target/checkout");
        anotherCheckoutDirectory = new File("target/anotherCheckout");
        testFile = new File("target/testFile.txt");
        FileUtils.writeStringToFile(testFile, "foo\n");

        // make a copy of svn repository
        FileUtils.copyDirectory(svnRepo, svnRepoCopy);
    }

    @After
    public void tearDown() throws Exception {
        closeStreams();
        FileUtils.deleteDirectory(checkoutDirectory);
        FileUtils.deleteDirectory(anotherCheckoutDirectory);
        if (testFile.exists()) {
            if (!testFile.delete()) {
                logger.error("Deletion of " + testFile.getPath() + " failed");
            }
        }

        // recover the svn repository from the copy
        subversionService.destroy();
        if (!svnRepoCopy.renameTo(svnRepo)) {
            logger.error("Rename of " + svnRepoCopy.getPath() + " to " + svnRepo.getPath()
                    + " failed");
        }
    }

    @Test
    public void ImportFile() throws Exception {
        long currentRevision = subversionService.getLatestRevision();
        long expected = currentRevision + 1;
        String newPath = documentRoot + "/" + UUID.randomUUID().toString();
        assertEquals(expected, subversionService.importFile(newPath, noteTestData));
    }

    @Test
    public void GetStream() throws Exception {
        String svnPath = documentRoot + "/notesTestData.txt";
        long revision = subversionService.importFile(svnPath, noteTestData);
        InputStream expected = register(new FileInputStream(noteTestData));
        InputStream actual = register(subversionService.getStream(svnPath, revision));
        boolean result = IOUtils.contentEquals(expected, actual);
        assertTrue(result);
    }

    @Test(expected = SubversionException.class)
    public void Delete() throws Exception {
        String svnPath = documentRoot + "/notesTestData.txt";
        long revision = subversionService.importFile(svnPath, noteTestData);
        InputStream expected = register(new FileInputStream(noteTestData));
        InputStream actual = register(subversionService.getStream(svnPath, revision));
        boolean result = IOUtils.contentEquals(expected, actual);
        assertTrue(result);
        subversionService.delete(svnPath);
        assertEquals(revision + 1, subversionService.getLatestRevision());
        register(subversionService.getStream(svnPath, -1));
    }

    @Test
    @Ignore
    public void GetRevisions() {
    }

    @Test
    public void GetEntries() {
        String svnPath = documentRoot;
        String filename = "testFile.txt";
        subversionService.importFile(svnPath + "/" + filename, testFile);
        Map<String, String> entries = subversionService.getEntries(svnPath, subversionService
                .getLatestRevision());
        assertTrue(entries.values().contains(filename));
        // There should be other files as well
        assertTrue(entries.size() > 1);
    }

    @Test
    public void Checkout() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        assertTrue(new File(checkoutDirectory + "/testFile.txt").exists());
    }

    // TODO This test is really slow, find out why.
    @Test
    public void Update() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        subversionService.update(updatedFile);

        assertTrue(FileUtils.contentEquals(modifiedFile, updatedFile));
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void Update_conflict() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        subversionService.update(updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);
    }

    @Test
    public void Commit() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        long oldRevision = subversionService.importFile(svnPath, testFile);
        subversionService.commit(svnPath, subversionService.getLatestRevision(), authService
                .getUsername(), new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    IOUtils.copy(source, target);
                    IOUtils.write("barfoooooofofofofoofoo", target);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        long currentRevision = subversionService.commit(svnPath, subversionService
                .getLatestRevision(), authService.getUsername(), new UpdateCallback() {
            @Override
            public void update(InputStream source, OutputStream target) {
                try {
                    IOUtils.copy(source, target);
                    IOUtils.write("jeeeeeeeejeeeejeeeeee", target);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            }
        });
        assertEquals(oldRevision + 2, currentRevision);
    }

    @Test
    @Ignore
    public void Commit_user_checkout_already_exists() throws Exception {
    }

    @Test
    @Ignore
    public void Commit_merge_conflict() throws Exception {
    }

    // TODO This test is really slow, find out why.
    @Test
    public void Commit_file() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = subversionService.commit(modifiedFile);
        InputStream modifiedStream = register(new FileInputStream(modifiedFile));
        assertTrue(IOUtils.contentEquals(modifiedStream, register(subversionService.getStream(
                svnPath, revision))));
    }

    // TODO This test is really slow, find out why.
    @Test(expected = RuntimeException.class)
    public void Commit_file_results_in_conflict() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        try {
            subversionService.commit(updatedFile);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Test
    public void GetLatestRevision() {
        long revision = subversionService.getLatestRevision();
        long expected = revision + 1;
        subversionService.importFile(documentRoot + "/foobar", noteTestData);
        assertEquals(expected, subversionService.getLatestRevision());
    }

    @Test
    public void GetLatestRevision_String() throws IOException {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = subversionService.commit(modifiedFile);
        assertEquals(revision, subversionService.getLatestRevision(svnPath));
    }

    @SuppressWarnings("deprecation")
    @Test(expected = SubversionException.class)
    public void Checkout_Throws_SubversionException() throws Exception {
        SVNClientManager clientManagerMock = createMock(SVNClientManager.class);
        subversionService.setClientManager(clientManagerMock);
        SVNUpdateClient updateClientMock = createMock(SVNUpdateClient.class);
        expect(clientManagerMock.getUpdateClient()).andReturn(updateClientMock);
        expect(
                updateClientMock.doCheckout(isA(SVNURL.class), (File) isNull(),
                        eq(SVNRevision.UNDEFINED), eq(SVNRevision.UNDEFINED), eq(true))).andThrow(
                createSvnException());
        replay(clientManagerMock, updateClientMock);
        subversionService.checkout(null, -1);
        verify(clientManagerMock, updateClientMock);
    }

    @Test(expected = SubversionException.class)
    public void Delete_Throws_SubversionException() throws Exception {
        SVNClientManager clientManagerMock = createMock(SVNClientManager.class);
        subversionService.setClientManager(clientManagerMock);
        SVNCommitClient commitClientMock = createMock(SVNCommitClient.class);
        expect(clientManagerMock.getCommitClient()).andReturn(commitClientMock);
        expect(commitClientMock.doDelete(isA(SVNURL[].class), isA(String.class))).andThrow(
                createSvnException());
        replay(clientManagerMock, commitClientMock);
        subversionService.delete("foo/bar");
        verify(clientManagerMock, commitClientMock);
    }

    private SVNException createSvnException() {
        return new SVNException(SVNErrorMessage.create(SVNErrorCode.REPOS_LOCKED));
    }

    @Test
    @Ignore
    public void Initialize() {
    }

    @Test
    @Ignore
    public void Destroy() {
    }

    @Test
    public void Root_Path_Contains_File_Items() {
        List<FileItem> items = subversionService.getFileItems(documentRoot, -1);
        assertFalse(items.isEmpty());
    }

    @Test
    public void Root_Path_Contains_File_Items_That_Have_Fields_Set() {
        List<FileItem> items = subversionService.getFileItems(documentRoot, -1);
        FileItem item = null;
        String title = "Nummisuutarit rakenteistettuna.xml";
        for (FileItem fi : items) {
            if (fi.getTitle().equals(title)) {
                item = fi;
                break;
            }
        }
        assertNotNull(item);
        assertEquals(title, item.getTitle());
        assertEquals(documentRoot + "/" + title, item.getPath());
        assertNull(item.getChildren());
        assertFalse(item.isFolder());
        assertFalse(item.isLazy());
    }

    @Inject
    @Symbol(EDITH.REPO_URL_PROPERTY)
    private String repositoryURL;

    @Test(expected = SubversionException.class)
    public void Get_File_Items_For_Root_Path_Throws_Exception_For_Path() throws Exception {
        SVNRepository repositoryMock = createMock(SVNRepository.class);
        SubversionServiceImpl versioningService = new SubversionServiceImpl(
                File.createTempFile("edith", null),
                svnRepo,
                repositoryURL,
                documentRoot,
                null);
        versioningService.setSvnRepository(repositoryMock);

        expect(repositoryMock.getDir(documentRoot, -1, false, Collections.emptyList())).andThrow(new SubversionException());

        replay(repositoryMock);
        versioningService.getFileItems(documentRoot, -1);
        verify(repositoryMock);
    }

    @Test
    public void Move_Bumps_Revision() {
        String oldPath = documentRoot + "/" + "Nummisuutarit rakenteistettuna.xml";
        String newPath = documentRoot + "/" + "Pummisuutarit.xml";
        long oldRevision = subversionService.getLatestRevision();
        assertEquals(oldRevision + 1, subversionService.move(oldPath, newPath));
    }
}
