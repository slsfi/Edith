package com.mysema.edith.ui.components.note;

import java.util.List;

import org.hibernate.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.mysema.edith.domain.DocumentNote;
import com.mysema.edith.domain.Note;
import com.mysema.edith.domain.NoteStatus;
import com.mysema.edith.domain.NoteType;
import com.mysema.edith.domain.Term;
import com.mysema.edith.domain.TermLanguage;
import com.mysema.edith.dto.SelectedText;
import com.mysema.edith.services.DocumentDao;
import com.mysema.edith.services.DocumentNoteDao;
import com.mysema.edith.services.NoteDao;
import com.mysema.edith.services.TermDao;
import com.mysema.edith.ui.pages.document.Annotate;
import com.sun.xml.internal.ws.api.PropertySet.Property;

@Import(library = { "NoteForm.js" })
@SuppressWarnings("unused")
public abstract class AbstractNoteForm {

    private static final String EDIT_ZONE = "editZone";

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @InjectPage
    private Annotate page;

    @Property
    @Parameter
    private SelectedText createTermSelection;

    @Inject
    private DocumentNoteDao documentNoteDao;

    @Inject
    private DocumentDao documentDao;

    @Inject
    private Messages messages;

    @Parameter
    private Note noteOnEdit;

    @Inject
    private NoteDao noteDao;

    @Property
    private boolean saveAsNew;

    @Property
    private boolean saveTermAsNew;

    @Parameter
    private List<DocumentNote> selectedNotes;

    @Inject
    private TermDao termDao;

    @Property
    private NoteType type;

    private boolean delete;

    void onSelectedFromDelete() {
        delete = true;
    }

    void onSelectedFromSaveAsNew() {
        saveAsNew = true;
    }

    public boolean isSlsMode() {
        return page.isSlsMode();
    }

    private Term getEditTerm(Note note) {
        return note.getTerm() != null ? note.getTerm() : new Term();
    }

    public Note getNoteOnEdit() {
        return noteOnEdit;
    }

    public void setNoteOnEdit(Note noteOnEdit) {
        this.noteOnEdit = noteOnEdit;
    }

    protected DocumentNoteDao getDocumentNoteDao() {
        return documentNoteDao;
    }

    public TermLanguage getLanguage() {
        return getEditTerm(noteOnEdit).getLanguage();
    }

    public EnumSelectModel getStatusModel() {
        final NoteStatus[] availableStatuses = noteOnEdit.getStatus().equals(NoteStatus.INITIAL) ? new NoteStatus[] {
                NoteStatus.INITIAL, NoteStatus.DRAFT, NoteStatus.FINISHED }
                : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
        return new EnumSelectModel(NoteStatus.class, messages, availableStatuses);
    }

    public int getTermInstances() {
        if (noteOnEdit.getTerm() != null) {
            return documentNoteDao.getOfTerm(noteOnEdit.getTerm().getId()).size();
        }
        return 0;
    }

    public NoteType[] getTypes() {
        return NoteType.values();
    }

    public boolean isSelected() {
        return noteOnEdit.getTypes().contains(type);
    }

    void onPrepareFromNoteEditForm(long noteId) {
        noteOnEdit = noteDao.getById(noteId);
    }

    List<Term> onProvideCompletionsFromBasicForm(String partial) {
        return termDao.findByStartOfBasicForm(partial, 10);
    }

    Object onSuccessFromNoteEditForm() {
        logger.info("onSuccessFromNoteEditForm begins with " + noteOnEdit);

        if (noteOnEdit.getTerm() != null) {
            logger.info("html edit contents: " + noteOnEdit.getTerm().getMeaning());
        }

        try {
            if (noteOnEdit.getStatus().equals(NoteStatus.INITIAL)) {
                noteOnEdit.setStatus(NoteStatus.DRAFT);
            }

            if (delete) {
                logger.info("note removed: " + noteOnEdit);
                noteDao.remove(noteOnEdit);
                page.getNoteEdit().setNoteOnEdit(null);
                return page.zoneWithInfo("delete-success").add("listZone", page.getSearchResults())
                        .add("noteEditZone", page.getNoteEdit());
            }
            logger.info("note saved: " + noteOnEdit);
            MultiZoneUpdate update = page.zoneWithInfo("submit-success");
            if (saveAsNew) {
                noteDao.saveAsNew(noteOnEdit);
                page.getNoteEdit().setNoteOnEdit(noteOnEdit);
                return page.zoneWithInfo("submit-success").add("listZone", page.getSearchResults())
                        .add("noteEditZone", page.getNoteEdit().getBlock());
            } else {
                noteDao.save(noteOnEdit);
                return update.add("listZone", page.getSearchResults());
            }

        } catch (Exception e) {
            return page.zoneWithError("note-addition-failed", e);
        }
    }

    public void setLanguage(TermLanguage language) {
        getEditTerm(noteOnEdit).setLanguage(language);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            noteOnEdit.getTypes().add(type);
        } else {
            noteOnEdit.getTypes().remove(type);
        }
    }

    @Validate("required")
    public void setStatus(NoteStatus status) {
        noteOnEdit.setStatus(status);
    }

    public boolean isDeletableNote() {
        return documentNoteDao.getDocumentNoteCount(noteOnEdit) == 0;
    }

    // Autocomplete
    public String getSearch() {
        return "";
    }

    public void setSearch(String search) {

    }

}
