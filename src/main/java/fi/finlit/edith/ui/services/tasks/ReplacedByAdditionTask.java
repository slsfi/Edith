package fi.finlit.edith.ui.services.tasks;

import java.util.List;

import org.apache.tapestry5.ioc.annotations.Inject;

import com.mysema.rdfbean.model.UID;
import com.mysema.rdfbean.object.Session;
import com.mysema.rdfbean.object.SessionCallback;
import com.mysema.rdfbean.object.SessionFactory;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.QDocumentNote;

public class ReplacedByAdditionTask implements Task{

    private final UID id = new UID(EDITH.NS, "ReplacedByAdditionTask");

    private final SessionFactory sessionFactory;

    public ReplacedByAdditionTask(@Inject SessionFactory sessionFactory) {
        this.sessionFactory = sessionFactory;
    }

    @Override
    public UID getId() {
        return id;
    }

    @Override
    public void run() {
        sessionFactory.execute(new SessionCallback<Void>(){
            @Override
            public Void doInSession(Session session) {
                runInternal(session);
                return null;
            }
        });
    }

    private void runInternal(Session session){
        QDocumentNote documentNote = QDocumentNote.documentNote;
        List<DocumentNote> documentNotes = session.from(documentNote)
            .orderBy(documentNote.localId.asc(), documentNote.svnRevision.asc())
            .list(documentNote);

        for (int i = 0; i < documentNotes.size(); i++){
            DocumentNote current = documentNotes.get(i);
            if (current.getReplacedBy() == null
                && (i < documentNotes.size() - 1)
                && current.getLocalId().equals(documentNotes.get(i+1).getLocalId())){
                current.setReplacedBy(documentNotes.get(i+1));
            }
        }
    }

}
