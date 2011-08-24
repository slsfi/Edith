package fi.finlit.edith.ui.components.note;

import java.util.List;

import org.apache.tapestry5.annotations.Import;
import org.apache.tapestry5.annotations.InjectPage;
import org.apache.tapestry5.annotations.Parameter;
import org.apache.tapestry5.annotations.Property;
import org.apache.tapestry5.beaneditor.Validate;
import org.apache.tapestry5.ioc.Messages;
import org.apache.tapestry5.ioc.annotations.Inject;
import org.apache.tapestry5.util.EnumSelectModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fi.finlit.edith.dto.SelectedText;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteFormat;
import fi.finlit.edith.sql.domain.NoteStatus;
import fi.finlit.edith.sql.domain.NoteType;
import fi.finlit.edith.sql.domain.Term;
import fi.finlit.edith.sql.domain.TermLanguage;
import fi.finlit.edith.ui.pages.document.Annotate;
import fi.finlit.edith.ui.services.DocumentDao;
import fi.finlit.edith.ui.services.DocumentNoteDao;
import fi.finlit.edith.ui.services.NoteDao;
import fi.finlit.edith.ui.services.TermDao;

@Import(library = {"NoteForm.js"})
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
        final NoteStatus[] availableStatuses = noteOnEdit.getStatus().equals(
                NoteStatus.INITIAL) ? new NoteStatus[] { NoteStatus.INITIAL, NoteStatus.DRAFT,
                NoteStatus.FINISHED } : new NoteStatus[] { NoteStatus.DRAFT, NoteStatus.FINISHED };
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
                return page.zoneWithInfo("delete-success")
                    .add("listZone", page.getSearchResults())
                    .add("noteEditZone", page.getNoteEdit());

            }
            logger.info("note saved: " + noteOnEdit);
            noteDao.save(noteOnEdit);
            return page.zoneWithInfo("submit-success").add("listZone", page.getSearchResults());
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

    //Autocomplete
    public String getSearch() {
        return "";
    }

    public void setSearch(String search) {

    }

}
