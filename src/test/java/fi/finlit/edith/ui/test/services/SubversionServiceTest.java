package fi.finlit.edith.ui.test.services;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.AuthService;
import fi.finlit.edith.ui.services.SubversionService;
import fi.finlit.edith.ui.services.SubversionServiceImpl;
import fi.finlit.edith.ui.services.UpdateCallback;

/**
 * SubversionServiceTest provides
 *
 * @author tiwe
 * @author vema
 * @version $Id$
 */
public class SubversionServiceTest extends AbstractServiceTest {

    private SubversionServiceImpl subversionService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject
    @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    @Inject
    @Symbol(EDITH.SVN_CACHE_DIR)
    private File svnCache;

    @Inject
    @Symbol(EDITH.REPO_FILE_PROPERTY)
    private File svnRepo;

    @Inject
    @Symbol(EDITH.REPO_URL_PROPERTY)
    private String repoURL;

    @Inject
    @Symbol(EDITH.MATERIAL_TEI_ROOT)
    private String materialTeiRoot;

    @Inject
    private AuthService authService;

    private File checkoutDirectory;

    private File anotherCheckoutDirectory;

    private File testFile;

    private File svnRepoCopy = new File("target/repoCopy");

    @Before
    public void setUp() throws Exception {
        subversionService = new SubversionServiceImpl(svnCache, svnRepo, repoURL, documentRoot, materialTeiRoot, authService);
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
        FileUtils.deleteDirectory(checkoutDirectory);
        FileUtils.deleteDirectory(anotherCheckoutDirectory);
        testFile.delete();

        // recover the svn repository from the copy
        subversionService.destroy();
        svnRepoCopy.renameTo(svnRepo);
    }

    @Test
    public void importFile() throws Exception {

        final long currentRevision = subversionService.getLatestRevision();
        final long expected = currentRevision + 1;
        String newPath = documentRoot + "/" + UUID.randomUUID().toString();
        assertEquals(expected, subversionService.importFile(newPath,
                noteTestData));
    }

    @Test
    public void getStream() throws Exception {
        final String svnPath = documentRoot + "/notesTestData.txt";
        final long revision = subversionService.importFile(svnPath, noteTestData);
        InputStream expected = new FileInputStream(noteTestData);
        InputStream actual = subversionService.getStream(svnPath, revision);
        boolean result = IOUtils.contentEquals(expected, actual);
        expected.close();
        actual.close();
        assertTrue(result);
    }

    @Test(expected = RuntimeException.class)
    public void delete() throws Exception {
        final String svnPath = documentRoot + "/notesTestData.txt";
        final long revision = subversionService.importFile(svnPath,
                noteTestData);
        InputStream expected = new FileInputStream(noteTestData);
        InputStream actual = subversionService.getStream(svnPath, revision);
        boolean result = IOUtils.contentEquals(expected, actual);
        expected.close();
        actual.close();
        assertTrue(result);
        subversionService.delete(svnPath);
        assertEquals(revision + 1, subversionService.getLatestRevision());
        // This will throw a RuntimeException because
        // the file isn't in the newest revision.
        subversionService.getStream(svnPath, -1);
    }

    @Test
    @Ignore
    public void getRevisions() {
    }

    @Test
    @Ignore
    public void getEntries() {
    }

    @Test
    public void checkout() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        assertTrue(new File(checkoutDirectory + "/testFile.txt").exists());
    }

    // TODO This test is really slow, find out why.
    @Test
    public void update() throws Exception {
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
    public void update_conflict() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
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
    public void commit() throws Exception {
        String svnPath = documentRoot + "/testFile.txt";
        long oldRevision = subversionService.importFile(svnPath, testFile);
        long newRevision = subversionService.commit(svnPath, subversionService.getLatestRevision(), new UpdateCallback() {
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
        assertEquals(oldRevision + 1, newRevision);
    }

    @Test
    @Ignore
    public void commit_user_checkout_already_exists() throws Exception {
    }

    @Test
    @Ignore
    public void commit_merge_conflict() throws Exception {
    }

    // TODO This test is really slow, find out why.
    @Test
    public void commit_file() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = subversionService.commit(modifiedFile);
        InputStream modifiedStream = new FileInputStream(modifiedFile);
        try {
            assertTrue(IOUtils.contentEquals(modifiedStream, subversionService.getStream(svnPath, revision)));
        } finally {
            modifiedStream.close();
        }
    }

    // TODO This test is really slow, find out why.
    @Test(expected = RuntimeException.class)
    public void commit_file_results_in_conflict() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
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
    @Ignore
    public void getLatestRevision() {
    }

    @Test
    @Ignore
    public void initialize() {
    }

    @Test
    @Ignore
    public void destroy() {
    }

    @Override
    protected Class<?> getServiceClass() {
        return SubversionService.class;
    }

}
