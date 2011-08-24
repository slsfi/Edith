package fi.finlit.edith.ui.services;

import java.io.File;
import java.util.Collection;
import java.util.List;

import org.apache.tapestry5.grid.GridDataSource;
import org.apache.tapestry5.hibernate.annotations.CommitAfter;

import fi.finlit.edith.dto.NoteSearchInfo;
import fi.finlit.edith.sql.domain.Document;
import fi.finlit.edith.sql.domain.DocumentNote;
import fi.finlit.edith.sql.domain.Note;
import fi.finlit.edith.sql.domain.NoteComment;

public interface NoteDao extends Dao<Note, Long> {

    /**
     * Creates a comment for the given note.
     *
     * @param concept
     * @param message
     */
    @CommitAfter
    NoteComment createComment(Note note, String message);

    @CommitAfter
    DocumentNote createDocumentNote(DocumentNote documentNote, Note note, Document document, String longText, int position);

    /**
     * Import notes from the given file
     *
     * @param file
     * @throws Exception
     */
    @CommitAfter
    int importNotes(File file);

    /**
     * Query for notes with the given search term
     *
     * @param searchTerm
     * @return
     */
    GridDataSource queryDictionary(String searchTerm);

    /**
     * Removes a NoteComment based on its id. Returns the deleted comment.
     *
     * @param commentId
     * @return
     */
    @CommitAfter
    NoteComment removeComment(Long commentId);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPersons(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryPlaces(String searchTerm);

    /**
     * @param searchTerm
     * @return
     */
    GridDataSource queryNotes(String searchTerm);

    /**
     * @return
     */
    List<Long> getOrphanIds();

    GridDataSource findNotes(NoteSearchInfo search);

    /**
     * @param editedNote
     */
    @CommitAfter
    void save(Note editedNote);

    /**
     * Remove notes
     *
     * @param notes
     */
    @CommitAfter
    void removeNotes(Collection<Note> notes);

    /**
     * Remove note
     *
     * @param note
     */
    @CommitAfter
    void remove(Note note);

    @CommitAfter
    DocumentNote createDocumentNote(Note note, Document document, String longText);

}
