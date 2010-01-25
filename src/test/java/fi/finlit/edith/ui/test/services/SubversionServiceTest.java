package fi.finlit.edith.ui.test.services;

import java.io.File;

import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;
import org.junit.Ignore;
import org.junit.Test;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.ui.services.SubversionService;

/**
 * SubversionServiceTest provides
 * 
 * @author tiwe
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

    @Test
    public void importFile() {
        subversionService.importFile(documentRoot + "/XXX", noteTestData);
    }

    @Test
    @Ignore
    public void getFile() {
    }

    @Test
    @Ignore
    public void delete() {
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
    public void update() {
    }

    @Test
    @Ignore
    public void commit() {
    }

    @Override
    protected Class<?> getServiceClass() {
        return SubversionService.class;
    }

}
