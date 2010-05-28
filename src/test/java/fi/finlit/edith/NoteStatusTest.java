package fi.finlit.edith;

import java.util.HashMap;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;

import com.mysema.rdfbean.model.Operation;
import com.mysema.rdfbean.model.ReplaceOperation;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.model.io.Format;
import com.mysema.rdfbean.model.io.RDFSource;
import com.mysema.rdfbean.sesame.MemoryRepository;

/**
 * Used to refactor NoteStatus from CamelCase to ALLCAPS.
 *
 * @author vema
 *
 */
public class NoteStatusTest {
    @Ignore
    @Test
    public void execute() {
        MemoryRepository repository = new MemoryRepository();
        repository.setDataDirName("target/data");
        repository.setSources(new RDFSource("classpath:/edith.ttl", Format.TURTLE, EDITH.NS));
        repository.initialize();
        Map<UID, UID> map = new HashMap<UID, UID>();
        map.put(new UID(EDITH.NS, "LockedForEdit"), new UID(EDITH.NS, "LOCKED_FOR_EDIT"));
        map.put(new UID(EDITH.NS, "Finished"), new UID(EDITH.NS, "FINISHED"));
        map.put(new UID(EDITH.NS, "Draft"), new UID(EDITH.NS, "DRAFT"));
        map.put(new UID(EDITH.NS, "Publishable"), new UID(EDITH.NS, "PUBLISHABLE"));
        map.put(new UID(EDITH.NS, "Initial"), new UID(EDITH.NS, "INITIAL"));
        Operation replace = new ReplaceOperation(map);
        repository.execute(replace);
        repository.close();
        // OutputStream os = new ByteArrayOutputStream();
        // repository.export(Format.TURTLE, os);
        // System.err.println(os.toString());
        // os.close();
    }

}
