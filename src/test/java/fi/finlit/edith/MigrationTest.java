package fi.finlit.edith;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;

import org.apache.commons.io.IOUtils;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.junit.Ignore;
import org.junit.Test;

import com.mysema.rdfbean.model.Format;
import com.mysema.rdfbean.model.Repository;
import com.mysema.rdfbean.sesame.MemoryRepository;

import fi.finlit.edith.ui.test.services.AbstractServiceTest;

@Ignore
public class MigrationTest extends AbstractServiceTest {
    @Inject
    private Repository repository;

    @Test
    public void Migrate() throws Exception {
        repository = new MemoryRepository(new File("edith"), false);
        repository.initialize();
        Format format = Format.TURTLE;

        OutputStream os = null;
        try {
            os = new FileOutputStream("db.turtle");
            repository.export(format, null, os);
        } finally {
            if (os != null) {
                IOUtils.closeQuietly(os);
            }
        }
        try {
            repository.load(Format.TURTLE, new FileInputStream("db.turtle"), null, false);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }
}
