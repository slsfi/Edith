package fi.finlit.edith.ui.services;

import com.mysema.rdfbean.dao.AbstractService;
import com.mysema.rdfbean.object.Session;

import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteRevision;
import fi.finlit.edith.domain.Term;

/**
 * AdminServiceImpl provides
 *
 * @author tiwe
 * @version $Id$
 */
public class AdminServiceImpl extends AbstractService implements AdminService{

    @Override
    public void removeNotes() {
        Session session = getSession();
        removeAll(session, NoteRevision.class);
        removeAll(session, Note.class);
    }

    @Override
    public void removeNotesAndTerms() {
        Session session = getSession();
        removeAll(session, NoteRevision.class);
        removeAll(session, Note.class);
        removeAll(session, Term.class);        
    }
    
    private <T> void removeAll(Session session, Class<T> type){
        for (T instance : session.findInstances(type)){
            session.delete(instance);
        }
    }

}
