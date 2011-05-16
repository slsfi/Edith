package fi.finlit.edith.ui.pages;

import java.io.IOException;

import org.apache.tapestry5.EventContext;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.SelectedText;
import fi.finlit.edith.domain.Term;
import fi.finlit.edith.ui.services.DocumentRepository;
import fi.finlit.edith.ui.services.NoteAdditionFailedException;

public class AddNote {

    @Inject
    private DocumentRepository documentRepository;
    
    @Inject 
    @Symbol(EDITH.EXTENDED_TERM)
    private boolean slsMode;
    
    @InjectPage
    private Index index;
    
    Index onActivate(EventContext context) throws IOException, NoteAdditionFailedException{
        String docId = context.get(String.class, 0);
        String startElement = context.get(String.class, 1);
        String text = context.get(String.class, 2);
        
        Document document = documentRepository.getById(docId);
        documentRepository.addNote(createNote(), document.getRevision(-1), new SelectedText(startElement, startElement, text));
        
        System.err.println("Added note");
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
