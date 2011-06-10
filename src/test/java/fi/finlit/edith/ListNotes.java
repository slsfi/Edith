package fi.finlit.edith;

import java.util.List;

import com.mysema.query.types.EntityPath;
import com.mysema.rdfbean.object.BeanQuery;
import com.mysema.rdfbean.object.BeanSubQuery;
import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactoryImpl;
import com.mysema.rdfbean.sesame.NativeRepository;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.QDocumentNote;
import fi.finlit.edith.domain.QNote;

public class ListNotes {

    public static void main(String[] args){
        NativeRepository repository = new NativeRepository();
        repository.setIndexes("spoc,posc,opsc");
        repository.setDataDirName("/opt/rdfbean/edith");

        Configuration configuration = new DefaultConfiguration(Note.class.getPackage());

        SessionFactoryImpl sessionFactory = new SessionFactoryImpl();
        sessionFactory.setConfiguration(configuration);
        sessionFactory.setRepository(repository);
        sessionFactory.initialize();

        try{
            Session session = sessionFactory.openSession();
            try{
                executeQueries(session);
            }finally{
                session.close();
            }

        }finally{
            sessionFactory.close();
        }

    }

    private static void executeQueries(Session session) {
        QNote note = QNote.note;
        QDocumentNote documentNote = QDocumentNote.documentNote;
        QDocumentNote otherNote = new QDocumentNote("other");

        // 1
        long start = System.currentTimeMillis();
        BeanQuery query = session.from(note)
            .where(sub(documentNote).where(
                documentNote.note().eq(note),
                documentNote.document().id.eq("2926"),
                documentNote.deleted.eq(false),
                sub(otherNote).where(otherNote.ne(documentNote),
                        otherNote.note().eq(documentNote.note()),
                        // FIXME: I commented this line out, is this a problem? -Vesa
//                        otherNote.localId.eq(documentNote.localId),
                        otherNote.createdOn.gt(documentNote.createdOn)).notExists()
                ).exists());

        List<Note> notes = query.list(note);
        System.out.println(notes.size() + " notes");
        System.out.println((System.currentTimeMillis()-start) + "ms");
        System.out.println();
        session.clear();

        // 2
        start = System.currentTimeMillis();
        query = session.from(note, documentNote)
            .where(
                documentNote.note().eq(note),
                documentNote.document().id.eq("2926"),
                documentNote.deleted.eq(false),
                sub(otherNote).where(otherNote.ne(documentNote),
                        otherNote.note().eq(note),
                     // FIXME: I commented this line out, is this a problem? -Vesa
//                        otherNote.localId.eq(documentNote.localId),
                        otherNote.createdOn.gt(documentNote.createdOn)).notExists()
                );

        notes = query.listDistinct(note);
        System.out.println(notes.size() + " notes");
        System.out.println((System.currentTimeMillis()-start) + "ms");

    }


    private static BeanSubQuery sub(EntityPath<?> entity) {
        return new BeanSubQuery().from(entity);
    }


}
