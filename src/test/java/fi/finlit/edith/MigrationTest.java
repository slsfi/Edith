package fi.finlit.edith;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;

import org.junit.Ignore;
import org.junit.Test;

import com.mysema.rdfbean.model.Format;
import com.mysema.rdfbean.sesame.MemoryRepository;
import com.mysema.rdfbean.sesame.NativeRepository;

@Ignore
public class MigrationTest {

    @Test
    public void Migrate() throws Exception {
        MemoryRepository memoryRepository = new MemoryRepository(new File("dataIn"));
        memoryRepository.initialize();
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        memoryRepository.export(Format.TURTLE, null, out);
        memoryRepository.close();

        NativeRepository nativeRepository = new NativeRepository(new File("dataOut"));
        nativeRepository.setIndexes("spoc,posc,cspo,opsc");
        nativeRepository.initialize();
        nativeRepository.load(Format.TURTLE, new ByteArrayInputStream(out.toByteArray()), null, false);
        nativeRepository.close();
    }
}
