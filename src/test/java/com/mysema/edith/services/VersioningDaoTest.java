/*
 * Copyright (c) 2018 Mysema
 */
package com.mysema.edith.services;

import com.google.inject.Inject;
import com.google.inject.name.Named;
import com.mysema.edith.EDITH;
import com.mysema.edith.EdithTestConstants;
import com.mysema.edith.dto.FileItem;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tmatesoft.svn.core.SVNErrorCode;
import org.tmatesoft.svn.core.SVNErrorMessage;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

import java.io.*;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.easymock.EasyMock.expect;
import static org.easymock.classextension.EasyMock.*;
import static org.junit.Assert.*;


public class VersioningDaoTest extends AbstractHibernateTest {

    private static final Logger logger = LoggerFactory.getLogger(VersioningDaoTest.class);

    @Inject
    private VersioningDaoImpl versioningDao;

    @Inject
    private AuthService authService;

    @Inject @Named(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject @Named(EdithTestConstants.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject @Named(EDITH.REPO_FILE_PROPERTY)
    private String svnRepoPath;

    private File svnRepo;

    private File checkoutDirectory;

    private File anotherCheckoutDirectory;

    private File testFile;

    private final File svnRepoCopy = new File("target/repoCopy");

    @Before
    public void setUp() throws Exception {
        svnRepo = new File(svnRepoPath);
        versioningDao.initialize();
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
        versioningDao.destroy();
        if (!svnRepoCopy.renameTo(svnRepo)) {
            logger.error("Rename of " + svnRepoCopy.getPath() + " to " + svnRepo.getPath()
                    + " failed");
        }
    }

    @Test
    public void ImportFile() throws Exception {
        long currentRevision = versioningDao.getLatestRevision();
        long expected = currentRevision + 1;
        String newPath = documentRoot + "/" + UUID.randomUUID().toString();
        assertEquals(expected, versioningDao.importFile(newPath, noteTestData));
    }

    @Test
    public void GetStream() throws Exception {
        String svnPath = documentRoot + "/notesTestData.txt";
        long revision = versioningDao.importFile(svnPath, noteTestData);
        InputStream expected = register(new FileInputStream(noteTestData));
        InputStream actual = register(versioningDao.getStream(svnPath, revision));
        boolean result = IOUtils.contentEquals(expected, actual);
        assertTrue(result);
    }

    @Test(expected = VersioningException.class)
    public void Delete() throws Exception {
        String svnPath = documentRoot + "/notesTestData.txt";
        long revision = versioningDao.importFile(svnPath, noteTestData);
        InputStream expected = register(new FileInputStream(noteTestData));
        InputStream actual = register(versioningDao.getStream(svnPath, revision));
        boolean result = IOUtils.contentEquals(expected, actual);
        assertTrue(result);
        versioningDao.delete(svnPath);
        assertEquals(revision + 1, versioningDao.getLatestRevision());
        register(versioningDao.getStream(svnPath, -1));
    }

    @Test
    @Ignore
    public void GetRevisions() {
    }

    @Test
    public void GetEntries() {
        String svnPath = documentRoot;
        String filename = "testFile.txt";
        versioningDao.importFile(svnPath + "/" + filename, testFile);
        Map<String, String> entries = versioningDao.getEntries(svnPath, versioningDao
                .getLatestRevision());
        assertTrue(entries.values().contains(filename));
        // There should be other files as well
        assertTrue(entries.size() > 1);
    }

    @Test
    public void Checkout() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        assertTrue(new File(checkoutDirectory + "/testFile.txt").exists());
    }

    // TODO This test is really slow, find out why.
    @Test
    public void Update() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        versioningDao.commit(modifiedFile);

        versioningDao.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        versioningDao.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        versioningDao.update(updatedFile);

        assertTrue(FileUtils.contentEquals(modifiedFile, updatedFile));
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void Update_Conflict() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        versioningDao.commit(modifiedFile);

        versioningDao.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        versioningDao.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        versioningDao.update(updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);
    }

    @Test
    public void Commit() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        long oldRevision = versioningDao.importFile(svnPath, testFile);
        versioningDao.commit(svnPath, versioningDao.getLatestRevision(), authService
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
        long currentRevision = versioningDao.commit(svnPath, versioningDao
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
    public void Commit_User_Checkout_Already_Exists() throws Exception {
    }

    @Test
    @Ignore
    public void Commit_Merge_Conflict() throws Exception {
    }

    // TODO This test is really slow, find out why.
    @Test
    public void Commit_File() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = versioningDao.commit(modifiedFile);
        InputStream modifiedStream = register(new FileInputStream(modifiedFile));
        assertTrue(IOUtils.contentEquals(modifiedStream, register(versioningDao.getStream(
                svnPath, revision))));
    }

    // TODO This test is really slow, find out why.
    @Test(expected = RuntimeException.class)
    public void Commit_File_Results_In_Conflict() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        versioningDao.commit(modifiedFile);

        versioningDao.checkout(anotherCheckoutDirectory, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        versioningDao.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        try {
            versioningDao.commit(updatedFile);
        } catch (RuntimeException e) {
            throw e;
        }
    }

    @Test
    public void GetLatestRevision() {
        long revision = versioningDao.getLatestRevision();
        long expected = revision + 1;
        versioningDao.importFile(documentRoot + "/foobar", noteTestData);
        assertEquals(expected, versioningDao.getLatestRevision());
    }

    @Test
    public void GetLatestRevision_String() throws IOException {
        String svnPath = documentRoot + "/testFile.txt";
        versioningDao.importFile(svnPath, testFile);
        versioningDao.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = versioningDao.commit(modifiedFile);
        assertEquals(revision, versioningDao.getLatestRevision());
    }

/*
    @SuppressWarnings("deprecation")
    @Test(expected = VersioningException.class)
    public void Checkout_Throws_SubversionException() throws Exception {
       // SVNClientManager clientManagerMock = createMock(SVNClientManager.class);
        SVNClientManager clientManagerMock = versioningDao.clientManagerForUser("Mock");
        SVNUpdateClient updateClientMock = createMock(SVNUpdateClient.class);
        expect(clientManagerMock.getUpdateClient()).andReturn(updateClientMock);
        expect(
                updateClientMock.doCheckout(isA(SVNURL.class), (File) isNull(),
                        eq(SVNRevision.UNDEFINED), eq(SVNRevision.UNDEFINED), eq(true))).andThrow(
                createSvnException());
        replay(clientManagerMock, updateClientMock);
        versioningDao.checkout(null, -1);
        verify(clientManagerMock, updateClientMock);
    }

    @Test(expected = VersioningException.class)
    public void Delete_Throws_SubversionException() throws Exception {
       // SVNClientManager clientManagerMock = createMock(SVNClientManager.class);
        SVNClientManager clientManagerMock = versioningDao.clientManagerForUser("Mock");
        SVNCommitClient commitClientMock = createMock(SVNCommitClient.class);
        expect(clientManagerMock.getCommitClient()).andReturn(commitClientMock);
        expect(commitClientMock.doDelete(isA(SVNURL[].class), isA(String.class))).andThrow(
                createSvnException());
        replay(clientManagerMock, commitClientMock);
        versioningDao.delete("foo/bar");
        verify(clientManagerMock, commitClientMock);
    }
    */

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
        List<FileItem> items = versioningDao.getFileItems(documentRoot, -1);
        assertFalse(items.isEmpty());
    }

    @Test
    public void Root_Path_Contains_File_Items_That_Have_Fields_Set() {
        List<FileItem> items = versioningDao.getFileItems(documentRoot, -1);
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
        assertFalse(item.getIsFolder());
        assertFalse(item.getIsLazy());
    }

    @Inject @Named(EDITH.REPO_URL_PROPERTY)
    private String repositoryURL;

    @Test(expected = VersioningException.class)
    public void Get_File_Items_For_Root_Path_Throws_Exception_For_Path() throws Exception {
        SVNRepository repositoryMock = createMock(SVNRepository.class);
        VersioningDaoImpl versioningService = new VersioningDaoImpl(
                File.createTempFile("edith", null).getAbsolutePath(),
                svnRepoPath,
                repositoryURL,
                documentRoot,
                null);
        versioningService.setSvnRepository(repositoryMock);

        expect(repositoryMock.getDir(documentRoot, -1, false, Collections.emptyList())).andThrow(new VersioningException());

        replay(repositoryMock);
        versioningService.getFileItems(documentRoot, -1);
        verify(repositoryMock);
    }

    @Test
    public void Move_Bumps_Revision() {
        String oldPath = documentRoot + "/" + "Nummisuutarit rakenteistettuna.xml";
        String newPath = documentRoot + "/" + "Pummisuutarit.xml";
        long oldRevision = versioningDao.getLatestRevision();
        assertEquals(oldRevision + 1, versioningDao.move(oldPath, newPath));
    }
}
