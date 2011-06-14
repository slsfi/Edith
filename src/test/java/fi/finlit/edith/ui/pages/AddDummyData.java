package fi.finlit.edith.ui.pages;

import java.util.List;

import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.dto.DocumentRevision;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.svn.RevisionInfo;

public class AddDummyData {

    @Inject
    private NoteDao noteRepository;
    
    @Inject
    private DocumentRepository documentRepository;
    
    @Inject 
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean slsMode;
    
    @InjectPage
    private Index index;
    
    Index onActivate(){
        Document document = documentRepository.getOrCreateDocumentForPath("/documents/trunk/Nummisuutarit rakenteistettuna.xml");
        List<RevisionInfo> revisions = documentRepository.getRevisions(document);
        long latestRevision = revisions.get(revisions.size() - 1).getSvnRevision();

        DocumentRevision docRev = document.getRevision(latestRevision);
        noteRepository.createDocumentNote(createNote(), docRev, "1", "l\u00E4htee h\u00E4ihins\u00E4 Mikko Vilkastuksen", 0);
        noteRepository.createDocumentNote(createNote(), docRev, "2", "koska suutarille k\u00E4skyn k\u00E4r\u00E4jiin annoit, saadaksesi naimalupaa.", 0);
        noteRepository.createDocumentNote(createNote(), docRev, "3", "tulee, niin seisoo s\u00E4\u00E4t\u00F6s-kirjassa.", 0);
        noteRepository.createDocumentNote(createNote(), docRev, "4", "kummallenkin m\u00E4\u00E4r\u00E4tty, niin emmep\u00E4 tiet\u00E4isi t\u00E4ss\u00E4", 0);
        System.err.println("Added dummy data");
        return index;
    }
    

    private Note createNote() {
        Note note = new Note();
        if (slsMode) {
            note.setTerm(new Term());
        }
        return note;
    }
    
}
