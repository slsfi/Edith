package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.BeginRender;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.annotations.SetupRender;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.ioc.annotations.Symbol;

import fi.finlit.edith.EDITH;
import fi.finlit.edith.domain.Concept;
import fi.finlit.edith.domain.Document;
import fi.finlit.edith.domain.DocumentNote;
import fi.finlit.edith.domain.DocumentNoteSearchInfo;
import fi.finlit.edith.domain.Note;
import fi.finlit.edith.domain.NoteType;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentNoteRepository;
import fi.finlit.edith.ui.services.NoteRepository;
import fi.finlit.edith.ui.services.NoteWithInstances;

@SuppressWarnings("unused")
public class SearchResults {

    @InjectPage
    private Annotate page;

    @Inject
    @Property
    private Block notesList;

    @Inject
    private NoteRepository noteRepository;

    @Property
    private List<NoteWithInstances> notesWithInstances;

    @Property
    private NoteWithInstances noteWithInstances;

    @Property
    private List<Note> notes;

    @Property
    private Note note;

    @Property
    private DocumentNote documentNote;

    @Inject
    private Messages messages;

    public Block getBlock() {
        return notesList;
    }

    // XXX Would be nice to get into real workflow
    public boolean getSearchResults() {

        System.out.println("Searching with" + page.getSearchInfo());

        // TODO Handle SKS case

        notes = noteRepository.findNotes(page.getSearchInfo());
        return notes != null && notes.size() > 0;
    }

    Object onActionFromSelectNote(String noteId) {
        System.out.println("select note " + noteId);

        page.getDocumentNotes().setNoteId(noteId);
        DocumentNote selected = page.getDocumentNotes().getSelectedNote();
        if (selected != null) {
            System.out.println("selected documentnote " + selected);
            page.getNoteEdit().setDocumentNoteOnEdit(selected);

        } else {
            page.getNoteEdit().setNoteOnEdit(noteRepository.getById(noteId));
        }
        return new MultiZoneUpdate("documentNotesZone", page.getDocumentNotes().getBlock()).add(
                "noteEditZone", page.getNoteEdit().getBlock());
    }

    public String getTypesString() {
        Collection<String> translated = new ArrayList<String>();
        for (NoteType t : note.getConcept(page.isSlsMode()).getTypes()) {
            translated.add(messages.get(t.toString()));
        }
        return StringUtils.join(translated, ", ");
    }

    public int getNumberOfInstancesInDocument() {
        return noteWithInstances.getDocumentNotes().size();
    }

    public Concept getNoteWithInstancesConcept() {
        return noteWithInstances.getNote().getConcept(page.isSlsMode());
    }

    public String getEditorsForNote() {
        return note.getEditors(page.isSlsMode());
    }

    public Concept getNoteConcept() {
        return note.getConcept(page.isSlsMode());
    }

    private enum DocumentNoteType {
        NORMAL, SEMI_ORPHAN, ORPHAN, ELSEWHERE;
    }

    public DocumentNoteType getDocumentNoteType() {
        if (documentNote.getDocument() == null) {
            return DocumentNoteType.ORPHAN;
        } else if (documentNote.getLongText() == null) {
            return DocumentNoteType.SEMI_ORPHAN;
        } else if (!documentNote.getDocument().equals(page.getDocument())) {
            return DocumentNoteType.ELSEWHERE;
        } else {
            return DocumentNoteType.NORMAL;
        }
    }

}