package fi.finlit.edith.ui.components.note;

import java.util.ArrayList;
import java.util.Collection;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.apache.tapestry5.Block;
import org.apache.tapestry5.ajax.MultiZoneUpdate;
import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectComponent;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.corelib.components.Grid;
import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;

import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteType;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.NoteDao;

@Import(library = {"SearchResults.js"})
@SuppressWarnings("unused")
public class SearchResults {

    @InjectPage
    private Annotate page;

    @Inject
    @Property
    private Block notesList;

    @Inject
    private NoteDao noteDao;

    @InjectComponent
    @Property
    private Grid grid;

    @Property
    private GridDataSource notes;

    @Property
    private Note note;

    @Property
    private DocumentNote documentNote;

    @Inject
    private Messages messages;

    public Block getBlock() {
        return notesList;
    }

    private static final Pattern STRIP_TAGS = Pattern.compile("\\<.*?>", Pattern.DOTALL);
    private static final int MAX_STRIPPED_LENGTH = 30;

    // XXX Would be nice to get into real workflow
    public boolean getSearchResults() {
        // TODO Handle SKS case

        notes = noteDao.findNotes(page.getSearchInfo());

        return notes != null && notes.getAvailableRows() > 0;
    }

    void onInplaceUpdate() {
        getSearchResults();
    }

    public int getPageSize() {
        //TODO make configurable
        return 20;
    }

    Object onActionFromSelectNote(long noteId) {
        page.getDocumentNotes().setNoteId(noteId);
        DocumentNote selected = page.getDocumentNotes().getSelectedNote();
        if (selected != null) {
            page.getNoteEdit().setDocumentNoteOnEdit(selected);

        } else {
            page.getNoteEdit().setNoteOnEdit(noteDao.getById(noteId));
        }
        return new MultiZoneUpdate("documentNotesZone", page.getDocumentNotes().getBlock()).add(
                "noteEditZone", page.getNoteEdit().getBlock());
    }

    public String getTypesString() {
        Collection<String> translated = new ArrayList<String>();
        for (NoteType t : note.getTypes()) {
            translated.add(messages.get(t.toString()));
        }
        return StringUtils.join(translated, ", ");
    }

    public String getStatusString() {
        return messages.get(note.getStatus().toString());
    }

    private String stripTagsAndConcat(String str, int maxSize) {
        if (str == null) {
            return null;
        }
        return StringUtils.abbreviate(STRIP_TAGS.matcher(str).replaceAll(""), maxSize);
    }

    public String getTermMeaning() {
        return stripTagsAndConcat(note.getTerm().getMeaning(), MAX_STRIPPED_LENGTH);
    }

    public String getDescription() {
        return stripTagsAndConcat(note.getDescription(), MAX_STRIPPED_LENGTH);
    }

    public String getEditorsForNote() {
        return note.getEditors();
    }

    private enum DocumentNoteType {
        NORMAL, SEMI_ORPHAN, ORPHAN, ELSEWHERE;
    }

    public DocumentNoteType getDocumentNoteType() {
        if (documentNote.getDocument() == null) {
            return DocumentNoteType.ORPHAN;
        } else if (documentNote.getFullSelection() == null) {
            return DocumentNoteType.SEMI_ORPHAN;
        } else if (!documentNote.getDocument().equals(page.getDocument())) {
            return DocumentNoteType.ELSEWHERE;
        } else {
            return DocumentNoteType.NORMAL;
        }
    }

}
