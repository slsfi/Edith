package fi.finlit.edith.ui.test.services;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.util.UUID;

import org.apache.commons.io.FileUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.After;
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

    @After
    public void tearDown() throws Exception {
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
        // the file isn't in the next revision.
        assertTrue(FileUtils.contentEquals(noteTestData, subversionService
                .getFile(svnPath, revision + 1)));
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
    @Ignore
    public void checkout() {

    }

    @Test
    @Ignore
    public void update() throws Exception {
        File file = new File("target/file.txt");
        FileUtils.writeStringToFile(file, "foo\n");
        final String svnPath = documentRoot + "/file.txt";
        File checkoutDirectory = new File("target/checkout");
        subversionService.importFile(svnPath, file);
        subversionService.checkout(checkoutDirectory);
        File modifiedFile = new File("target/checkout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(svnPath, modifiedFile);

        File anotherCheckoutDirectory = new File("target/anotherCheckout");
        subversionService.checkout(anotherCheckoutDirectory);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(svnPath, modifiedFile);

        File updatedFile = new File("target/anotherCheckout/" + documentRoot + "/file.txt");
        subversionService.update(svnPath, updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);

        // TODO Not the best place to remove the checkout folder or the file
        FileUtils.deleteDirectory(checkoutDirectory);
        FileUtils.deleteDirectory(anotherCheckoutDirectory);
        file.delete();
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void update_conflict() throws Exception {
        File file = new File("target/file.txt");
        FileUtils.writeStringToFile(file, "foo\n");
        final String svnPath = documentRoot + "/file.txt";
        File checkoutDirectory = new File("target/checkout");
        subversionService.importFile(svnPath, file);
        subversionService.checkout(checkoutDirectory);
        File modifiedFile = new File("target/checkout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(svnPath, modifiedFile);

        File anotherCheckoutDirectory = new File("target/anotherCheckout");
        subversionService.checkout(anotherCheckoutDirectory);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(svnPath, modifiedFile);

        File updatedFile = new File("target/anotherCheckout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        subversionService.update(svnPath, updatedFile);

        FileUtils.contentEquals(modifiedFile, updatedFile);

        // TODO Not the best place to remove the checkout folder or the file
        FileUtils.deleteDirectory(checkoutDirectory);
        FileUtils.deleteDirectory(anotherCheckoutDirectory);
        file.delete();
    }

    @Test
    @Ignore
    public void commit() throws Exception {
        File file = new File("target/file.txt");
        FileUtils.writeStringToFile(file, "foo\n");
        final String svnPath = documentRoot + "/file.txt";
        File checkoutDirectory = new File("target/checkout");
        subversionService.importFile(svnPath, file);
        subversionService.checkout(checkoutDirectory);
        File modifiedFile = new File("target/checkout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        long revision = subversionService.commit(svnPath, modifiedFile);
        assertTrue(FileUtils.contentEquals(modifiedFile, subversionService.getFile(svnPath, revision)));
        // TODO Not the best place to remove the checkout folder or the file
        FileUtils.deleteDirectory(checkoutDirectory);
        file.delete();
    }

    @Test(expected = RuntimeException.class)
    @Ignore
    public void commit_conflict() throws Exception {
        File file = new File("target/file.txt");
        FileUtils.writeStringToFile(file, "foo\n");
        final String svnPath = documentRoot + "/file.txt";
        File checkoutDirectory = new File("target/checkout");
        subversionService.importFile(svnPath, file);
        subversionService.checkout(checkoutDirectory);
        File modifiedFile = new File("target/checkout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\n");
        subversionService.commit(svnPath, modifiedFile);

        File anotherCheckoutDirectory = new File("target/anotherCheckout");
        subversionService.checkout(anotherCheckoutDirectory);

        FileUtils.writeStringToFile(modifiedFile, "foo\nbar\nbaz\nfoobar");
        subversionService.commit(svnPath, modifiedFile);

        File updatedFile = new File("target/anotherCheckout/" + documentRoot + "/file.txt");
        FileUtils.writeStringToFile(updatedFile, "jeejee");
        try {
            subversionService.commit(svnPath, updatedFile);
        } catch (RuntimeException e) {
            throw e;
        } finally {
            // TODO Still not the best place to remove the checkout folder or the file
            FileUtils.deleteDirectory(checkoutDirectory);
            FileUtils.deleteDirectory(anotherCheckoutDirectory);
            file.delete();
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
