package fi.finlit.edith.ui.test.services;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.SubversionService;

/**
 * SubversionServiceTest provides
 *
 * @author tiwe
 * @author vema
 * @version $Id$
 */
public class SubversionServiceTest extends AbstractServiceTest {
    @Inject
    private SubversionService subversionService;

    @Inject
    @Symbol(EDITH.SVN_DOCUMENT_ROOT)
    private String documentRoot;

    @Inject
    @Symbol(ServiceTestModule.NOTE_TEST_DATA_KEY)
    private File noteTestData;

    private File checkoutDirectory;
    private File anotherCheckoutDirectory;
    private File testFile;

    @Before
    public void setUp() throws Exception {
        checkoutDirectory = new File("target/checkout");
        anotherCheckoutDirectory = new File("target/anotherCheckout");
        testFile = new File("target/testFile.txt");
        FileUtils.writeStringToFile(testFile, "foo\n");
    }

    @After
    public void tearDown() throws Exception {
        FileUtils.deleteDirectory(checkoutDirectory);
        FileUtils.deleteDirectory(anotherCheckoutDirectory);
        testFile.delete();
        subversionService.destroy();
        subversionService.initialize();
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
    public void getFile() throws Exception {
        final String svnPath = documentRoot + "/notesTestData.txt";
        final long revision = subversionService.importFile(svnPath,
                noteTestData);
        assertTrue(FileUtils.contentEquals(noteTestData, subversionService
                .getFile(svnPath, revision)));
    }

    @Test(expected = RuntimeException.class)
    public void delete() throws Exception {
        final String svnPath = documentRoot + "/notesTestData.txt";
        final long revision = subversionService.importFile(svnPath,
                noteTestData);
        assertTrue(FileUtils.contentEquals(noteTestData, subversionService
                .getFile(svnPath, revision)));
        subversionService.delete(svnPath);
        assertEquals(revision + 1, subversionService.getLatestRevision());
        // This will throw a RuntimeException because
        // the file isn't in the newest revision.
        subversionService.getFile(svnPath, -1);
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
        subversionService.checkout(checkoutDirectory, documentRoot, -1);
        assertTrue(new File(checkoutDirectory + "/testFile.txt").exists());
    }

    // TODO This test is really slow, find out why.
    @Test
    public void update() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, documentRoot, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, documentRoot, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        subversionService.update(updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void update_conflict() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, documentRoot, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, documentRoot, -1);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(modifiedFile);

        File updatedFile = new File(anotherCheckoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        subversionService.update(updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);
    }

    // TODO This test is really slow, find out why.
    @Test
    public void commit() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, documentRoot, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = subversionService.commit(modifiedFile);
        assertTrue(FileUtils.contentEquals(modifiedFile, subversionService.getFile(svnPath, revision)));
    }

    // TODO This test is really slow, find out why.
    @Test(expected = RuntimeException.class)
    public void commit_conflict() throws Exception {
        final String svnPath = documentRoot + "/testFile.txt";
        subversionService.importFile(svnPath, testFile);
        subversionService.checkout(checkoutDirectory, documentRoot, -1);
        File modifiedFile = new File(checkoutDirectory + "/testFile.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(modifiedFile);

        subversionService.checkout(anotherCheckoutDirectory, documentRoot, -1);

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
