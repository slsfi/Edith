package fi.finlit.edith;

import java.util.Map;

import com.mysema.commons.lang.CloseableIterator;
import com.mysema.rdfbean.model.Blocks;
import com.mysema.rdfbean.model.NODE;
import com.mysema.rdfbean.model.QNODE;
import com.mysema.rdfbean.model.RDFConnection;
import com.mysema.rdfbean.model.RDFQuery;
import com.mysema.rdfbean.model.RDFQueryImpl;
import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.sesame.NativeRepository;

public class FixDocumentNotes {

    public static void main(String[] args){
        NativeRepository repository = new NativeRepository();
        repository.setIndexes("spoc,posc,opsc");
        repository.setDataDirName("/opt/rdfbean/edith");
        repository.initialize();

        try{
           RDFConnection connection = repository.openConnection();
            try{
                RDFQuery query = new RDFQueryImpl(connection);
                UID DocumentNote = new UID(EDITH.NS, "DocumentNote");
                UID note = new UID(EDITH.NS, "note");
                CloseableIterator<Map<String,NODE>> nodes = query.where(
                        Blocks.S_TYPE,
                        Blocks.SPOC.exists().not())
                    .set(QNODE.type, DocumentNote)
                    .set(QNODE.p, note)
                    .select(QNODE.s);

                try{
                    while (nodes.hasNext()) {
                        Map<String, NODE> row = nodes.next();
                        System.err.println(row);
                    }
                }finally{
                    nodes.close();
                }

            }finally{
                connection.close();
            }

        }finally{
            repository.close();
        }

    }

}
