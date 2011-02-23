package fi.finlit.edith;

import com.mysema.rdfbean.object.Configuration;
import com.mysema.rdfbean.object.DefaultConfiguration;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionFactoryImpl;
import com.mysema.rdfbean.sesame.NativeRepository;

import fi.finlit.edith.domain.Note;

public class FixData {

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
//                changeParagraphsIntoStrings(session);
            }finally{
                session.close();
            }

        }finally{
            sessionFactory.close();
        }

    }

//    private static void changeParagraphsIntoStrings(Session session) {
//        // notes
//        List<Note> notes = session.findInstances(Note.class);
//        for (Note note : notes){
//            if (note.description != null){
//                note.setDescriptionString(note.description.toString());
//                session.delete(note.description);
//                note.description = null;
//            }
//
//            if (note.sources != null){
//                note.setSourcesString(note.sources.toString());
//                session.delete(note.sources);
//                note.sources = null;
//            }
//
//            if (note.subtextSources != null){
//                note.setSubtextSourcesString(note.subtextSources.toString());
//                session.delete(note.subtextSources);
//                note.subtextSources = null;
//            }
//
//            session.save(note);
//        }
//
//        session.clear();
//
//        // paragraphs
//        List<Paragraph> paragraphs = session.findInstances(Paragraph.class);
//        for (Paragraph paragraph : paragraphs){
//            session.delete(paragraph);
//        }
//        session.clear();
//
//        // elements
//        List<ParagraphElement> elements = session.findInstances(ParagraphElement.class);
//        for (ParagraphElement element : elements){
//            session.delete(element);
//        }
//        session.clear();
//
//    }

}
